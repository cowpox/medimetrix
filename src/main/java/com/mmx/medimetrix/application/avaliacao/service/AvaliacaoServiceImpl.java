package com.mmx.medimetrix.application.avaliacao.service;

import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoCreate;
import com.mmx.medimetrix.application.avaliacao.commands.AvaliacaoUpdate;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoEncerradaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoJaPublicadaException;
import com.mmx.medimetrix.application.avaliacao.exceptions.AvaliacaoNaoEncontradaException;
import com.mmx.medimetrix.application.avaliacao.port.out.AvaliacaoDao;
import com.mmx.medimetrix.application.avaliacaoquestao.port.out.AvaliacaoQuestaoDao;
import com.mmx.medimetrix.application.participacao.port.out.ParticipacaoDao;
import com.mmx.medimetrix.domain.core.Avaliacao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AvaliacaoServiceImpl implements AvaliacaoService {

    private final AvaliacaoDao dao;
    private final AvaliacaoQuestaoDao avaliacaoQuestaoDao;
    private final ParticipacaoDao participacaoDao;

    public AvaliacaoServiceImpl(AvaliacaoDao dao,
                                AvaliacaoQuestaoDao avaliacaoQuestaoDao,
                                ParticipacaoDao participacaoDao) {
        this.dao = dao;
        this.avaliacaoQuestaoDao = avaliacaoQuestaoDao;
        this.participacaoDao = participacaoDao;
    }

    @Override
    public Long create(AvaliacaoCreate cmd) {
        Avaliacao a = new Avaliacao();
        a.setTitulo(cmd.getTitulo());
        a.setDataInicioAplic(cmd.getDataInicioAplic());
        a.setDataFimAplic(cmd.getDataFimAplic());
        a.setkMinimo(cmd.getkMinimo());
        a.setStatus(cmd.getStatus());
        a.setAtivo(cmd.getAtivo() != null ? cmd.getAtivo() : Boolean.TRUE);
        return dao.insert(a);
    }

    @Override
    public void update(Long id, AvaliacaoUpdate cmd) {
        Avaliacao atual = dao.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        // Regras de negócio básicas
        if ("ENCERRADA".equalsIgnoreCase(atual.getStatus())) {
            throw new AvaliacaoEncerradaException();
        }
        if ("PUBLICADA".equalsIgnoreCase(atual.getStatus())) {
            // ajuste conforme sua política; abaixo um exemplo mais restritivo
            if (cmd.titulo() != null || cmd.dataInicioAplic() != null || cmd.dataFimAplic() != null
                    || cmd.kMinimo() != null || cmd.status() != null) {
                throw new AvaliacaoJaPublicadaException();
            }
        }

        // Aplicação dos campos (somente se enviados)
        if (cmd.titulo() != null) atual.setTitulo(cmd.titulo());
        if (cmd.dataInicioAplic() != null) atual.setDataInicioAplic(cmd.dataInicioAplic());
        if (cmd.dataFimAplic() != null) atual.setDataFimAplic(cmd.dataFimAplic());
        if (cmd.kMinimo() != null) atual.setkMinimo(cmd.kMinimo());
        if (cmd.status() != null) atual.setStatus(cmd.status());
        if (cmd.ativo() != null) atual.setAtivo(cmd.ativo());

        // Validação de datas se ambas presentes após merge
        if (atual.getDataInicioAplic() != null && atual.getDataFimAplic() != null
                && atual.getDataInicioAplic().isAfter(atual.getDataFimAplic())) {
            throw new IllegalArgumentException("DATA_INICIO_APLIC deve ser ≤ DATA_FIM_APLIC.");
        }

        int rows = dao.update(atual);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }


    @Override
    @Transactional(readOnly = true)
    public Optional<Avaliacao> findById(Long id) {
        return dao.findById(id);
    }

    // ====== Listagens paginadas ======
    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> listPaged(int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.listPaged(offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> listByStatus(String status, int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.listByStatus(status, offset, size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Avaliacao> searchByTituloLike(String termo, int page, int size) {
        int offset = Math.max(page, 0) * Math.max(size, 1);
        return dao.searchByTituloLikePaged(termo, offset, size);
    }

    @Override
    public void ativar(Long id) {
        int rows = dao.reactivate(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void desativar(Long id) {
        int rows = dao.deactivate(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void delete(Long id) {
        int rows = dao.deleteById(id);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void publicar(Long id) {
        Avaliacao atual = dao.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        String status = atual.getStatus();

        if ("PUBLICADA".equalsIgnoreCase(status)) {
            throw new AvaliacaoJaPublicadaException();
        }
        if ("ENCERRADA".equalsIgnoreCase(status)) {
            // opcional: exception específica
            throw new AvaliacaoEncerradaException();
        }

        // Datas coerentes antes de publicar
        if (atual.getDataInicioAplic() != null && atual.getDataFimAplic() != null
                && atual.getDataInicioAplic().isAfter(atual.getDataFimAplic())) {
            throw new IllegalArgumentException("DATA_INICIO_APLIC deve ser ≤ DATA_FIM_APLIC.");
        }

        // (a) precisa ter pelo menos 1 questão na avaliação
        int totalQuestoes = avaliacaoQuestaoDao.countByAvaliacao(id);
        if (totalQuestoes == 0) {
            throw new IllegalStateException("A avaliação precisa ter ao menos uma questão para ser publicada.");
            // se quiser, depois troca por AvaliacaoJaPublicadaException ou outra específica
        }

        // (b) precisa ter participações geradas
        int totalParticipacoes = participacaoDao.countByAvaliacao(id);
        if (totalParticipacoes == 0) {
            throw new IllegalStateException("Gere as participações antes de publicar a avaliação.");
        }

        atual.setStatus("PUBLICADA");
        int rows = dao.update(atual);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();
    }

    @Override
    public void encerrar(Long id) {
        Avaliacao atual = dao.findById(id)
                .orElseThrow(AvaliacaoNaoEncontradaException::new);

        String status = atual.getStatus();

        if ("ENCERRADA".equalsIgnoreCase(status)) {
            // já está encerrada – pode silenciosamente retornar ou lançar exception
            throw new AvaliacaoEncerradaException();
        }
        if (!"PUBLICADA".equalsIgnoreCase(status)) {
            // regra de negócio: só se encerra algo PUBLICADA
            throw new IllegalStateException("A avaliação só pode ser encerrada a partir do status PUBLICADA.");
        }

        atual.setStatus("ENCERRADA");
        int rows = dao.update(atual);
        if (rows == 0) throw new AvaliacaoNaoEncontradaException();

        // Se quiser, depois: aqui é o ponto para travar novas respostas na camada de aplicação.
    }

}
