package com.mmx.medimetrix.application.meta.service;

import com.mmx.medimetrix.application.meta.commands.MetaCreate;
import com.mmx.medimetrix.application.meta.commands.MetaUpdate;
import com.mmx.medimetrix.application.meta.port.out.MetaDao;
import com.mmx.medimetrix.application.meta.queries.MetaFiltro;
import com.mmx.medimetrix.domain.core.Meta;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class MetaServiceImpl implements MetaService {

    private final MetaDao dao;

    public MetaServiceImpl(MetaDao dao) { this.dao = dao; }

    @Override
    public Meta create(MetaCreate cmd) {
        if (cmd == null || cmd.idCriterio() == null || cmd.alvo() == null || !StringUtils.hasText(cmd.operador())) {
            throw new IllegalArgumentException("Critério, alvo e operador são obrigatórios.");
        }
        Meta m = new Meta();
        m.setIdCriterio(cmd.idCriterio());
        m.setIdUnidade(cmd.idUnidade());
        m.setIdEspecialidade(cmd.idEspecialidade());
        m.setAlvo(cmd.alvo());
        m.setOperador(cmd.operador());
        m.setVigenciaInicio(cmd.vigenciaInicio());
        m.setVigenciaFim(cmd.vigenciaFim());
        m.setPrioridade(cmd.prioridade());
        m.setJustificativa(cmd.justificativa());
        m.setAtivo(true);
        Long id = dao.insert(m);
        return dao.findById(id).orElseThrow(() -> new NoSuchElementException("Meta não encontrada após inserir."));
    }

    @Override
    public Meta update(Long id, MetaUpdate cmd) {
        Meta atual = dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Meta não encontrada."));

        if (cmd.idCriterio() != null)     atual.setIdCriterio(cmd.idCriterio());
        if (cmd.idUnidade() != null)      atual.setIdUnidade(cmd.idUnidade());
        if (cmd.idEspecialidade() != null)atual.setIdEspecialidade(cmd.idEspecialidade());
        if (cmd.alvo() != null)           atual.setAlvo(cmd.alvo());
        if (cmd.operador() != null)       atual.setOperador(cmd.operador());
        if (cmd.vigenciaInicio() != null) atual.setVigenciaInicio(cmd.vigenciaInicio());
        if (cmd.vigenciaFim() != null)    atual.setVigenciaFim(cmd.vigenciaFim());
        if (cmd.ativo() != null)          atual.setAtivo(cmd.ativo());
        if (cmd.prioridade() != null)     atual.setPrioridade(cmd.prioridade());
        if (cmd.justificativa() != null)  atual.setJustificativa(cmd.justificativa());

        dao.update(atual);
        return dao.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Meta não encontrada após atualizar."));
    }

    @Override public Optional<Meta> findById(Long id) { return dao.findById(id); }

    @Override
    public List<Meta> list(MetaFiltro filtro) {
        int page = filtro.page() != null && filtro.page() > 0 ? filtro.page() : 0;
        int size = filtro.size() != null && filtro.size() > 0 ? filtro.size() : 20;
        int offset = page * size;

        // 1) se vier só idCriterio
        if (filtro.idCriterio() != null
                && filtro.idUnidade() == null
                && filtro.idEspecialidade() == null) {
            return dao.listByCriterio(filtro.idCriterio(), offset, size);
        }

        // 2) se vier qualquer combinação de escopo
        if (filtro.idCriterio() != null
                || filtro.idUnidade() != null
                || filtro.idEspecialidade() != null) {
            return dao.listByEscopo(
                    filtro.idCriterio(),
                    filtro.idUnidade(),
                    filtro.idEspecialidade(),
                    offset, size
            );
        }

        // 3) fallback: vigentes na data (se não informada, usa hoje)
        java.time.LocalDate data = (filtro.naData() != null) ? filtro.naData() : java.time.LocalDate.now();
        return dao.listAtivasVigentesEm(data, offset, size);
    }

    @Override public void activate(Long id) { dao.reactivate(id); }
    @Override public void deactivate(Long id) { dao.deactivate(id); }
}
