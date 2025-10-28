package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.domain.core.Meta;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MetaRowMapper implements RowMapper<Meta> {
    @Override
    public Meta mapRow(ResultSet rs, int rowNum) throws SQLException {
        Meta m = new Meta();
        m.setIdMeta(rs.getLong("ID_META"));
        m.setIdCriterio(rs.getLong("ID_CRITERIO"));
        long un = rs.getLong("ID_UNIDADE");
        m.setIdUnidade(rs.wasNull() ? null : un);
        long es = rs.getLong("ID_ESPECIALIDADE");
        m.setIdEspecialidade(rs.wasNull() ? null : es);
        m.setAlvo(rs.getBigDecimal("ALVO"));
        m.setOperador(rs.getString("OPERADOR"));
        m.setVigenciaInicio(rs.getDate("VIGENCIA_INICIO") != null ? rs.getDate("VIGENCIA_INICIO").toLocalDate() : null);
        m.setVigenciaFim(rs.getDate("VIGENCIA_FIM") != null ? rs.getDate("VIGENCIA_FIM").toLocalDate() : null);
        m.setAtivo(rs.getBoolean("ATIVO"));
        int pr = rs.getInt("PRIORIDADE");
        m.setPrioridade(rs.wasNull() ? null : pr);
        m.setJustificativa(rs.getString("JUSTIFICATIVA"));
        m.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        m.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return m;
    }
}
