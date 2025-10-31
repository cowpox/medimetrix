package com.mmx.medimetrix.infrastructure.dao.jdbc.unidade;

import com.mmx.medimetrix.domain.core.Unidade;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UnidadeRowMapper implements RowMapper<Unidade> {
    @Override
    public Unidade mapRow(ResultSet rs, int rowNum) throws SQLException {
        Unidade u = new Unidade();
        u.setIdUnidade(rs.getLong("ID_UNIDADE"));
        u.setNome(rs.getString("NOME"));
        u.setGestorUsuarioId(rs.getLong("GESTOR_USUARIO_ID"));
        u.setAtivo(rs.getBoolean("ATIVO"));
        u.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        u.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return u;
    }
}
