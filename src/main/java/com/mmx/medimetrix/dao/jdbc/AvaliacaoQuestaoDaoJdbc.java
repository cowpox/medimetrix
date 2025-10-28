package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.dao.AvaliacaoQuestaoDao;
import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class AvaliacaoQuestaoDaoJdbc implements AvaliacaoQuestaoDao {

    private final JdbcTemplate jdbc;
    private static final AvaliacaoQuestaoRowMapper MAPPER = new AvaliacaoQuestaoRowMapper();

    public AvaliacaoQuestaoDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_AVALIACAO, ID_QUESTAO, ORDEM, OBRIGATORIA_NA_AVAL, ATIVA_NA_AVAL, PESO_NA_AVAL, VISIVEL_PARA_GESTOR_LOCAL, OBSERVACAO_ADMIN, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.AVALIACAO_QUESTAO";

    @Override
    public int insert(AvaliacaoQuestao aq) {
        final String sql = "INSERT INTO MEDIMETRIX.AVALIACAO_QUESTAO (ID_AVALIACAO, ID_QUESTAO, ORDEM, OBRIGATORIA_NA_AVAL, ATIVA_NA_AVAL, PESO_NA_AVAL, VISIVEL_PARA_GESTOR_LOCAL, OBSERVACAO_ADMIN) VALUES (?, ?, ?, ?, COALESCE(?, TRUE), ?, ?, ?)";
        return jdbc.update(sql,
                aq.getIdAvaliacao(), aq.getIdQuestao(), aq.getOrdem(), aq.getObrigatoriaNaAval(), aq.getAtivaNaAval(), aq.getPesoNaAval(), aq.getVisivelParaGestorLocal(), aq.getObservacaoAdmin());
    }

    @Override
    public int update(AvaliacaoQuestao aq) {
        final String sql = "UPDATE MEDIMETRIX.AVALIACAO_QUESTAO SET ORDEM = ?, OBRIGATORIA_NA_AVAL = ?, ATIVA_NA_AVAL = ?, PESO_NA_AVAL = ?, VISIVEL_PARA_GESTOR_LOCAL = ?, OBSERVACAO_ADMIN = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?";
        return jdbc.update(sql,
                aq.getOrdem(), aq.getObrigatoriaNaAval(), aq.getAtivaNaAval(), aq.getPesoNaAval(), aq.getVisivelParaGestorLocal(), aq.getObservacaoAdmin(),
                aq.getIdAvaliacao(), aq.getIdQuestao());
    }

    @Override
    public Optional<AvaliacaoQuestao> findOne(Long idAvaliacao, Long idQuestao) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?";
        List<AvaliacaoQuestao> list = jdbc.query(sql, MAPPER, idAvaliacao, idQuestao);
        return list.stream().findFirst();
    }

    @Override
    public List<AvaliacaoQuestao> listByAvaliacao(Long idAvaliacao) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ? ORDER BY ORDEM ASC";
        return jdbc.query(sql, MAPPER, idAvaliacao);
    }

    @Override
    public List<AvaliacaoQuestao> listByQuestao(Long idQuestao) {
        final String sql = BASE_SELECT + " WHERE ID_QUESTAO = ? ORDER BY ID_AVALIACAO, ORDEM ASC";
        return jdbc.query(sql, MAPPER, idQuestao);
    }

    @Override
    public int deleteOne(Long idAvaliacao, Long idQuestao) {
        final String sql = "DELETE FROM MEDIMETRIX.AVALIACAO_QUESTAO WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?";
        return jdbc.update(sql, idAvaliacao, idQuestao);
    }

    @Override
    public int deleteAllByAvaliacao(Long idAvaliacao) {
        final String sql = "DELETE FROM MEDIMETRIX.AVALIACAO_QUESTAO WHERE ID_AVALIACAO = ?";
        return jdbc.update(sql, idAvaliacao);
    }

    @Override
    @Transactional
    public int swapOrdem(Long idAvaliacao, Long idQuestaoA, Long idQuestaoB) {
        // Troca atomica de ORDEM entre duas questões da mesma avaliação
        final String getSql = "SELECT ORDEM FROM MEDIMETRIX.AVALIACAO_QUESTAO WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?";
        Integer ordemA = jdbc.queryForObject(getSql, Integer.class, idAvaliacao, idQuestaoA);
        Integer ordemB = jdbc.queryForObject(getSql, Integer.class, idAvaliacao, idQuestaoB);

        final String upd = "UPDATE MEDIMETRIX.AVALIACAO_QUESTAO SET ORDEM = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?";
        int r1 = jdbc.update(upd, ordemB, idAvaliacao, idQuestaoA);
        int r2 = jdbc.update(upd, ordemA, idAvaliacao, idQuestaoB);
        return r1 + r2;
    }
}
