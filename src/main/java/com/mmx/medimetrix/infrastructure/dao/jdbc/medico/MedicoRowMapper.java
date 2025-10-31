package com.mmx.medimetrix.infrastructure.dao.jdbc.medico;

import com.mmx.medimetrix.domain.core.Medico;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicoRowMapper implements RowMapper<Medico> {
    @Override
    public Medico mapRow(ResultSet rs, int rowNum) throws SQLException {
        Medico m = new Medico();
        m.setUsuarioId(rs.getLong("USUARIO_ID"));
        m.setEspecialidadeId(rs.getLong("ESPECIALIDADE_ID"));
        m.setUnidadeId(rs.getLong("UNIDADE_ID"));
        m.setCrmNumero(rs.getString("CRM_NUMERO"));
        m.setCrmUf(rs.getString("CRM_UF"));
        m.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        m.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return m;
    }
}
