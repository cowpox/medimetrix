package com.mmx.medimetrix.infrastructure.dao.jdbc.questao;

import com.mmx.medimetrix.application.questao.port.out.QuestaoDao;
import com.mmx.medimetrix.domain.core.Questao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class QuestaoDaoJdbc implements QuestaoDao {

    private final JdbcTemplate jdbc;
    private static final QuestaoRowMapper MAPPER = new QuestaoRowMapper();

    public QuestaoDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_QUESTAO, ID_CRITERIO, ENUNCIADO, DESCRICAO_AUXILIAR, ATIVO, TIPO, OBRIGATORIEDADE, VALIDACAO_NUM_MIN, VALIDACAO_NUM_MAX, TAMANHO_TEXTO_MAX, SENSIVEL, VISIVEL_PARA_GESTOR, VERSAO, ORDEM_SUGERIDA, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.QUESTAO";

    @Override
    public Long insert(Questao q) {
        final String sql = "INSERT INTO MEDIMETRIX.QUESTAO (ID_CRITERIO, ENUNCIADO, DESCRICAO_AUXILIAR, ATIVO, TIPO, OBRIGATORIEDADE, VALIDACAO_NUM_MIN, VALIDACAO_NUM_MAX, TAMANHO_TEXTO_MAX, SENSIVEL, VISIVEL_PARA_GESTOR, VERSAO, ORDEM_SUGERIDA) VALUES (?, ?, ?, COALESCE(?, TRUE), ?, COALESCE(?, 'OBRIGATORIA'), ?, ?, ?, COALESCE(?, FALSE), COALESCE(?, TRUE), COALESCE(?, 1), ?) RETURNING ID_QUESTAO";
        return jdbc.queryForObject(sql, Long.class,
                q.getIdCriterio(), q.getEnunciado(), q.getDescricaoAuxiliar(), q.getAtivo(), q.getTipo(), q.getObrigatoriedade(),
                q.getValidacaoNumMin(), q.getValidacaoNumMax(), q.getTamanhoTextoMax(), q.getSensivel(), q.getVisivelParaGestor(), q.getVersao(), q.getOrdemSugerida());
    }

    @Override
    public int update(Questao q) {
        final String sql = "UPDATE MEDIMETRIX.QUESTAO SET ID_CRITERIO = ?, ENUNCIADO = ?, DESCRICAO_AUXILIAR = ?, ATIVO = ?, TIPO = ?, OBRIGATORIEDADE = ?, VALIDACAO_NUM_MIN = ?, VALIDACAO_NUM_MAX = ?, TAMANHO_TEXTO_MAX = ?, SENSIVEL = ?, VISIVEL_PARA_GESTOR = ?, VERSAO = ?, ORDEM_SUGERIDA = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_QUESTAO = ?";
        return jdbc.update(sql,
                q.getIdCriterio(), q.getEnunciado(), q.getDescricaoAuxiliar(), q.getAtivo(), q.getTipo(), q.getObrigatoriedade(),
                q.getValidacaoNumMin(), q.getValidacaoNumMax(), q.getTamanhoTextoMax(), q.getSensivel(), q.getVisivelParaGestor(), q.getVersao(), q.getOrdemSugerida(), q.getIdQuestao());
    }

    @Override
    public Optional<Questao> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_QUESTAO = ?";
        List<Questao> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Questao> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY ORDEM_SUGERIDA NULLS LAST, ID_QUESTAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Questao> listAtivasByTipo(String tipo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ATIVO = TRUE AND TIPO = ? ORDER BY ORDEM_SUGERIDA NULLS LAST, ID_QUESTAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, tipo, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Questao> listByCriterio(Long idCriterio, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_CRITERIO = ? ORDER BY ORDEM_SUGERIDA NULLS LAST, ID_QUESTAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idCriterio, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Questao> searchByEnunciadoLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE UNACCENT(LOWER(ENUNCIADO)) LIKE UNACCENT(LOWER(?)) ORDER BY ORDEM_SUGERIDA NULLS LAST, ID_QUESTAO LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public boolean hasUso(Long idQuestao) {
        // Verifica vínculo com avaliação
        Integer count1 = jdbc.queryForObject(
                "SELECT COUNT(*) FROM MEDIMETRIX.AVALIACAO_QUESTAO WHERE ID_QUESTAO = ?",
                Integer.class,
                idQuestao
        );

        // Verifica respostas existentes
        Integer count2 = jdbc.queryForObject(
                "SELECT COUNT(*) FROM MEDIMETRIX.RESPOSTA WHERE ID_QUESTAO = ?",
                Integer.class,
                idQuestao
        );

        return (count1 != null && count1 > 0) || (count2 != null && count2 > 0);
    }


    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.QUESTAO SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_QUESTAO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.QUESTAO SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_QUESTAO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.QUESTAO WHERE ID_QUESTAO = ?";
        return jdbc.update(sql, id);
    }
}
