package com.mmx.medimetrix.web.mvc.controller.gestor;

import com.mmx.medimetrix.application.avaliacao.service.AvaliacaoService;
import com.mmx.medimetrix.application.participacao.service.ParticipacaoService;
import com.mmx.medimetrix.application.resposta.service.RespostaService;
import com.mmx.medimetrix.application.usuario.service.UsuarioService;
import com.mmx.medimetrix.domain.core.Avaliacao;
import com.mmx.medimetrix.domain.core.Participacao;
import com.mmx.medimetrix.domain.core.Resposta;
import com.mmx.medimetrix.domain.core.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/app/gestor/relatorios/avaliacoes")
public class RelatoriosAvaliacaoPageController {

    private static final DateTimeFormatter DF = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final int BIG_SIZE = 1000;

    private final AvaliacaoService avaliacaoService;
    private final ParticipacaoService participacaoService;
    private final RespostaService respostaService;
    private final UsuarioService usuarioService;

    public RelatoriosAvaliacaoPageController(AvaliacaoService avaliacaoService,
                                             ParticipacaoService participacaoService,
                                             RespostaService respostaService,
                                             UsuarioService usuarioService) {
        this.avaliacaoService = avaliacaoService;
        this.participacaoService = participacaoService;
        this.respostaService = respostaService;
        this.usuarioService = usuarioService;
    }

    // ================================
    // Lista de Avaliações (Relatórios)
    // ================================
    @GetMapping
    public String list(@RequestParam(name = "termo", required = false) String termo,
                       @RequestParam(name = "status", required = false) String status,
                       Model model) {

        model.addAttribute("pageTitle", "Relatórios de Avaliações");
        model.addAttribute("breadcrumb", "Relatórios");
        model.addAttribute("termo", termo);
        model.addAttribute("status", status);

        List<Avaliacao> todas = avaliacaoService.listPaged(0, BIG_SIZE);

        // só avaliações que fazem sentido para relatório
        List<Avaliacao> filtradas = todas.stream()
                .filter(a -> !"RASCUNHO".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        // filtro por status
        if (status != null && !status.isBlank()) {
            filtradas = filtradas.stream()
                    .filter(a -> status.equalsIgnoreCase(a.getStatus()))
                    .collect(Collectors.toList());
        }

        // filtro por título
        if (termo != null && !termo.isBlank()) {
            String t = termo.trim().toLowerCase();
            filtradas = filtradas.stream()
                    .filter(a -> a.getTitulo() != null &&
                            a.getTitulo().toLowerCase().contains(t))
                    .collect(Collectors.toList());
        }

        List<AvaliacaoResumoVM> avaliacoes = new ArrayList<>();

        for (Avaliacao a : filtradas) {
            List<Participacao> parts =
                    participacaoService.listByAvaliacao(a.getIdAvaliacao(), 0, BIG_SIZE);

            long total = parts.size();
            long respondidas = parts.stream()
                    .filter(p -> "RESPONDIDA".equals(p.getStatus()))
                    .count();
            long emAndamento = parts.stream()
                    .filter(p -> "EM_ANDAMENTO".equals(p.getStatus()))
                    .count();
            long pendentes = parts.stream()
                    .filter(p -> "PENDENTE".equals(p.getStatus()))
                    .count();

            BigDecimal adesao = BigDecimal.ZERO;
            if (total > 0) {
                adesao = BigDecimal
                        .valueOf((respondidas * 100.0) / total)
                        .setScale(1, RoundingMode.HALF_UP);
            }

            String periodo = "-";
            if (a.getDataInicioAplic() != null && a.getDataFimAplic() != null) {
                periodo = a.getDataInicioAplic().format(DF) +
                        " a " +
                        a.getDataFimAplic().format(DF);
            }

            avaliacoes.add(new AvaliacaoResumoVM(
                    a.getIdAvaliacao(),
                    a.getTitulo(),
                    periodo,
                    a.getStatus(),
                    total,
                    respondidas,
                    emAndamento,
                    pendentes,
                    adesao
            ));
        }

        // ordena por título
        avaliacoes.sort(Comparator.comparing(AvaliacaoResumoVM::titulo));

        model.addAttribute("avaliacoes", avaliacoes);

        return "gestor/relatorios-avaliacoes-list";
    }

    // ================================
    // Relatório Consolidado da Avaliação
    // ================================
    @GetMapping("/{id}")
    public String detalhe(@PathVariable Long id, Model model) {

        Avaliacao avaliacao = avaliacaoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Avaliação não encontrada"));

        model.addAttribute("pageTitle", "Relatório da Avaliação");
        model.addAttribute("breadcrumb", "Relatórios");

        List<Participacao> parts =
                participacaoService.listByAvaliacao(id, 0, BIG_SIZE);

        long total = parts.size();
        long respondidas = parts.stream()
                .filter(p -> "RESPONDIDA".equals(p.getStatus()))
                .count();
        long emAndamento = parts.stream()
                .filter(p -> "EM_ANDAMENTO".equals(p.getStatus()))
                .count();
        long pendentes = parts.stream()
                .filter(p -> "PENDENTE".equals(p.getStatus()))
                .count();

        BigDecimal adesao = BigDecimal.ZERO;
        if (total > 0) {
            adesao = BigDecimal
                    .valueOf((respondidas * 100.0) / total)
                    .setScale(1, RoundingMode.HALF_UP);
        }

        String periodo = "-";
        if (avaliacao.getDataInicioAplic() != null && avaliacao.getDataFimAplic() != null) {
            periodo = avaliacao.getDataInicioAplic().format(DF) +
                    " a " +
                    avaliacao.getDataFimAplic().format(DF);
        }

        AvaliacaoResumoVM resumo = new AvaliacaoResumoVM(
                avaliacao.getIdAvaliacao(),
                avaliacao.getTitulo(),
                periodo,
                avaliacao.getStatus(),
                total,
                respondidas,
                emAndamento,
                pendentes,
                adesao
        );

        // monta tabela por médico (simples)
        List<ParticipacaoResumoVM> medicos = new ArrayList<>();

        for (Participacao p : parts) {

            Long idMedico = p.getAvaliadoMedicoId();

            String nomeMedico = "(Médico " + idMedico + ")";
            try {
                Usuario u = usuarioService.getById(idMedico);
                if (u != null) {
                    nomeMedico = u.getNome();
                }
            } catch (Exception ignore) {
            }

            // nota média simples: média de valorNumerico da participação
            List<Resposta> respostas = respostaService.listByParticipacao(p.getIdParticipacao());
            List<BigDecimal> notas = respostas.stream()
                    .map(Resposta::getValorNumerico)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            BigDecimal media = null;
            if (!notas.isEmpty()) {
                BigDecimal soma = notas.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                media = soma
                        .divide(BigDecimal.valueOf(notas.size()), 2, RoundingMode.HALF_UP);
            }

            medicos.add(new ParticipacaoResumoVM(
                    p.getIdParticipacao(),
                    idMedico,
                    nomeMedico,
                    p.getStatus(),
                    media
            ));
        }

        // ordena médicos por nome
        medicos.sort(Comparator.comparing(ParticipacaoResumoVM::nomeMedico));

        model.addAttribute("avaliacao", avaliacao);
        model.addAttribute("resumo", resumo);
        model.addAttribute("participacoes", medicos);

        return "gestor/relatorios-avaliacao-detalhe";
    }

    // ================================
    // View Models
    // ================================
    public record AvaliacaoResumoVM(
            Long id,
            String titulo,
            String periodo,
            String status,
            long totalParticipacoes,
            long respondidas,
            long emAndamento,
            long pendentes,
            BigDecimal adesaoPercent
    ) {

        public String statusLabel() {
            return switch (status == null ? "" : status.toUpperCase()) {
                case "PUBLICADA" -> "Publicada";
                case "ENCERRADA" -> "Encerrada";
                case "RASCUNHO"  -> "Rascunho";
                default -> status;
            };
        }

        public String statusBadgeClass() {
            return switch (status == null ? "" : status.toUpperCase()) {
                case "PUBLICADA" -> "bg-primary text-white";
                case "ENCERRADA" -> "bg-secondary text-white";
                case "RASCUNHO"  -> "bg-warning text-dark";
                default -> "bg-light text-dark";
            };
        }
    }

    public record ParticipacaoResumoVM(
            Long idParticipacao,
            Long idMedico,
            String nomeMedico,
            String status,
            BigDecimal notaMedia
    ) {}
}
