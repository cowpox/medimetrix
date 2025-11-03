package com.mmx.medimetrix.infrastructure.dao.jdbc.avaliacaoquestao;

import com.mmx.medimetrix.application.avaliacaoquestao.port.out.AvaliacaoQuestaoDao;
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
        if (idQuestaoA.equals(idQuestaoB)) return 0;

        // 1) Trava as duas linhas e lê as ordens
        final String lockSql = """
        SELECT ID_QUESTAO, ORDEM
        FROM MEDIMETRIX.AVALIACAO_QUESTAO
        WHERE ID_AVALIACAO = ? AND ID_QUESTAO IN (?, ?)
        FOR UPDATE
        """;
        var rows = jdbc.query(lockSql,
                (rs, i) -> new Object[]{ rs.getLong("ID_QUESTAO"), rs.getInt("ORDEM") },
                idAvaliacao, idQuestaoA, idQuestaoB);

        if (rows.size() < 2) return 0; // 404 no service

        Integer ordemA = null, ordemB = null;
        for (Object[] r : rows) {
            Long idQ = (Long) r[0];
            Integer ord = (Integer) r[1];
            if (idQ.equals(idQuestaoA)) ordemA = ord;
            else if (idQ.equals(idQuestaoB)) ordemB = ord;
        }
        if (ordemA == null || ordemB == null) return 0;

        // 2) Sentinela garantidamente livre (evita corrida com MAX+1)
        final int TEMP = 2_147_483_647; // Integer.MAX_VALUE

        // 3) A -> TEMP
        int r1 = jdbc.update("""
        UPDATE MEDIMETRIX.AVALIACAO_QUESTAO
        SET ORDEM = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
        WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?
        """, TEMP, idAvaliacao, idQuestaoA);

        // 4) B -> ordemA
        int r2 = jdbc.update("""
        UPDATE MEDIMETRIX.AVALIACAO_QUESTAO
        SET ORDEM = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
        WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?
        """, ordemA, idAvaliacao, idQuestaoB);

        // 5) A -> ordemB
        int r3 = jdbc.update("""
        UPDATE MEDIMETRIX.AVALIACAO_QUESTAO
        SET ORDEM = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
        WHERE ID_AVALIACAO = ? AND ID_QUESTAO = ?
        """, ordemB, idAvaliacao, idQuestaoA);

        return r1 + r2 + r3; // esperado: 3
    }



}
