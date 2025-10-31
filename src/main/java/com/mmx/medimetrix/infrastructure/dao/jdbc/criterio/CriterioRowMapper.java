package com.mmx.medimetrix.infrastructure.dao.jdbc.criterio;

import com.mmx.medimetrix.domain.core.Criterio;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CriterioRowMapper implements RowMapper<Criterio> {
    @Override
    public Criterio mapRow(ResultSet rs, int rowNum) throws SQLException {
        Criterio c = new Criterio();
        c.setIdCriterio(rs.getLong("ID_CRITERIO"));
        c.setNome(rs.getString("NOME"));
        c.setDefinicaoOperacional(rs.getString("DEFINICAO_OPERACIONAL"));
        c.setDescricao(rs.getString("DESCRICAO"));
        c.setAtivo(rs.getBoolean("ATIVO"));
        c.setPeso(rs.getBigDecimal("PESO"));
        int ordem = rs.getInt("ORDEM_SUGERIDA");
        c.setOrdemSugerida(rs.wasNull() ? null : ordem);
        int versao = rs.getInt("VERSAO");
        c.setVersao(rs.wasNull() ? null : versao);
        c.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        c.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return c;
    }
}
