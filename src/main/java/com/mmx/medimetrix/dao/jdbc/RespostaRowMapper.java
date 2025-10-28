package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.domain.core.Resposta;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RespostaRowMapper implements RowMapper<Resposta> {
    @Override
    public Resposta mapRow(ResultSet rs, int rowNum) throws SQLException {
        Resposta r = new Resposta();
        r.setIdResposta(rs.getLong("ID_RESPOSTA"));
        r.setIdParticipacao(rs.getLong("ID_PARTICIPACAO"));
        r.setIdAvaliacao(rs.getLong("ID_AVALIACAO"));
        r.setIdQuestao(rs.getLong("ID_QUESTAO"));
        r.setValorNumerico(rs.getBigDecimal("VALOR_NUMERICO"));
        Object bin = rs.getObject("VALOR_BINARIO");
        r.setValorBinario(bin != null ? rs.getBoolean("VALOR_BINARIO") : null);
        r.setTexto(rs.getString("TEXTO"));
        r.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        return r;
    }
}
