package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.domain.core.Especialidade;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EspecialidadeRowMapper implements RowMapper<Especialidade> {
    @Override
    public Especialidade mapRow(ResultSet rs, int rowNum) throws SQLException {
        Especialidade e = new Especialidade();
        e.setIdEspecialidade(rs.getLong("ID_ESPECIALIDADE"));
        e.setNome(rs.getString("NOME"));
        e.setAtivo(rs.getBoolean("ATIVO"));
        e.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        e.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return e;
    }
}
