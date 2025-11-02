package com.mmx.medimetrix.infrastructure.dao.jdbc.resposta;

import com.mmx.medimetrix.application.resposta.port.out.RespostaDao;
import com.mmx.medimetrix.domain.core.Resposta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class RespostaDaoJdbc implements RespostaDao {

    private final JdbcTemplate jdbc;
    private static final RespostaRowMapper MAPPER = new RespostaRowMapper();

    public RespostaDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_RESPOSTA, ID_PARTICIPACAO, ID_AVALIACAO, ID_QUESTAO, VALOR_NUMERICO, VALOR_BINARIO, TEXTO, DATA_CRIACAO FROM MEDIMETRIX.RESPOSTA";

    @Override
    public Long insert(Resposta r) {
        final String sql = "INSERT INTO MEDIMETRIX.RESPOSTA (ID_PARTICIPACAO, ID_AVALIACAO, ID_QUESTAO, VALOR_NUMERICO, VALOR_BINARIO, TEXTO) VALUES (?, ?, ?, ?, ?, ?) RETURNING ID_RESPOSTA";
        return jdbc.queryForObject(sql, Long.class, r.getIdParticipacao(), r.getIdAvaliacao(), r.getIdQuestao(), r.getValorNumerico(), r.getValorBinario(), r.getTexto());
    }

    @Override
    public int updateValores(Resposta r) {
        final String sql = "UPDATE MEDIMETRIX.RESPOSTA SET VALOR_NUMERICO = ?, VALOR_BINARIO = ?, TEXTO = ? WHERE ID_RESPOSTA = ?";
        return jdbc.update(sql, r.getValorNumerico(), r.getValorBinario(), r.getTexto(), r.getIdResposta());
    }

    @Override
    public Optional<Resposta> findById(Long idResposta) {
        final String sql = BASE_SELECT + " WHERE ID_RESPOSTA = ?";
        List<Resposta> list = jdbc.query(sql, MAPPER, idResposta);
        return list.stream().findFirst();
    }

    @Override
    public List<Resposta> listByParticipacao(Long idParticipacao) {
        final String sql = BASE_SELECT + " WHERE ID_PARTICIPACAO = ? ORDER BY ID_RESPOSTA";
        return jdbc.query(sql, MAPPER, idParticipacao);
    }

    @Override
    public List<Resposta> listByAvaliacao(Long idAvaliacao, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ? ORDER BY ID_RESPOSTA LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idAvaliacao, limit != null ? limit : 200, offset != null ? offset : 0);
    }

    @Override
    public List<Resposta> listByQuestao(Long idQuestao, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_QUESTAO = ? ORDER BY ID_RESPOSTA LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idQuestao, limit != null ? limit : 200, offset != null ? offset : 0);
    }

    @Override
    public List<Resposta> listPaged(Integer offset, Integer limit) {
        final String sql =
                BASE_SELECT + " ORDER BY ID_RESPOSTA DESC LIMIT ? OFFSET ?"; // use seu BASE_SELECT
        return jdbc.query(sql, MAPPER, limit, offset);
    }


    @Override
    public int deleteById(Long idResposta) {
        final String sql = "DELETE FROM MEDIMETRIX.RESPOSTA WHERE ID_RESPOSTA = ?";
        return jdbc.update(sql, idResposta);
    }

    @Override
    public int deleteByParticipacao(Long idParticipacao) {
        final String sql = "DELETE FROM MEDIMETRIX.RESPOSTA WHERE ID_PARTICIPACAO = ?";
        return jdbc.update(sql, idParticipacao);
    }
}
