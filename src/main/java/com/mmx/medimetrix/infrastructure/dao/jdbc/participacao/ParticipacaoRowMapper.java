package com.mmx.medimetrix.infrastructure.dao.jdbc.participacao;

import com.mmx.medimetrix.domain.core.Participacao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ParticipacaoRowMapper implements RowMapper<Participacao> {
    @Override
    public Participacao mapRow(ResultSet rs, int rowNum) throws SQLException {
        Participacao p = new Participacao();
        p.setIdParticipacao(rs.getLong("ID_PARTICIPACAO"));
        p.setIdAvaliacao(rs.getLong("ID_AVALIACAO"));
        p.setAvaliadoMedicoId(rs.getLong("AVALIADO_MEDICO_ID"));
        long un = rs.getLong("UNIDADE_SNAPSHOT_ID");
        p.setUnidadeSnapshotId(rs.wasNull() ? null : un);
        long es = rs.getLong("ESPECIALIDADE_SNAPSHOT_ID");
        p.setEspecialidadeSnapshotId(rs.wasNull() ? null : es);
        p.setStatus(rs.getString("STATUS"));
        p.setStartedAt(rs.getTimestamp("STARTED_AT") != null ? rs.getTimestamp("STARTED_AT").toLocalDateTime() : null);
        p.setLastActivityAt(rs.getTimestamp("LAST_ACTIVITY_AT") != null ? rs.getTimestamp("LAST_ACTIVITY_AT").toLocalDateTime() : null);
        p.setSubmittedAt(rs.getTimestamp("SUBMITTED_AT") != null ? rs.getTimestamp("SUBMITTED_AT").toLocalDateTime() : null);
        p.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        p.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return p;
    }
}
