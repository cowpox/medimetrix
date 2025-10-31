package com.mmx.medimetrix.infrastructure.dao.jdbc.participacao;

import com.mmx.medimetrix.application.participacao.port.out.ParticipacaoDao;
import com.mmx.medimetrix.domain.core.Participacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ParticipacaoDaoJdbc implements ParticipacaoDao {

    private final JdbcTemplate jdbc;
    private static final ParticipacaoRowMapper MAPPER = new ParticipacaoRowMapper();

    public ParticipacaoDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_PARTICIPACAO, ID_AVALIACAO, AVALIADO_MEDICO_ID, UNIDADE_SNAPSHOT_ID, ESPECIALIDADE_SNAPSHOT_ID, STATUS, STARTED_AT, LAST_ACTIVITY_AT, SUBMITTED_AT, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.PARTICIPACAO";

    @Override
    public Long insert(Participacao p) {
        final String sql = "INSERT INTO MEDIMETRIX.PARTICIPACAO (ID_AVALIACAO, AVALIADO_MEDICO_ID, UNIDADE_SNAPSHOT_ID, ESPECIALIDADE_SNAPSHOT_ID, STATUS) VALUES (?, ?, ?, ?, COALESCE(?, 'PENDENTE')) RETURNING ID_PARTICIPACAO";
        return jdbc.queryForObject(sql, Long.class, p.getIdAvaliacao(), p.getAvaliadoMedicoId(), p.getUnidadeSnapshotId(), p.getEspecialidadeSnapshotId(), p.getStatus());
    }

    @Override
    public int update(Participacao p) {
        final String sql = "UPDATE MEDIMETRIX.PARTICIPACAO SET ID_AVALIACAO = ?, AVALIADO_MEDICO_ID = ?, UNIDADE_SNAPSHOT_ID = ?, ESPECIALIDADE_SNAPSHOT_ID = ?, STATUS = ?, STARTED_AT = ?, LAST_ACTIVITY_AT = ?, SUBMITTED_AT = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_PARTICIPACAO = ?";
        return jdbc.update(sql, p.getIdAvaliacao(), p.getAvaliadoMedicoId(), p.getUnidadeSnapshotId(), p.getEspecialidadeSnapshotId(), p.getStatus(), p.getStartedAt(), p.getLastActivityAt(), p.getSubmittedAt(), p.getIdParticipacao());
    }

    @Override
    public Optional<Participacao> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_PARTICIPACAO = ?";
        List<Participacao> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Participacao> findByAvaliacaoAndMedico(Long idAvaliacao, Long medicoId) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ? AND AVALIADO_MEDICO_ID = ?";
        List<Participacao> list = jdbc.query(sql, MAPPER, idAvaliacao, medicoId);
        return list.stream().findFirst();
    }

    @Override
    public List<Participacao> listByAvaliacao(Long idAvaliacao, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ? ORDER BY ID_PARTICIPACAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idAvaliacao, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Participacao> listByMedico(Long medicoId, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE AVALIADO_MEDICO_ID = ? ORDER BY ID_PARTICIPACAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, medicoId, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Participacao> listByStatus(String status, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE STATUS = ? ORDER BY ID_PARTICIPACAO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, status, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int markStarted(Long idParticipacao) {
        final String sql = "UPDATE MEDIMETRIX.PARTICIPACAO SET STATUS = 'EM_ANDAMENTO', STARTED_AT = COALESCE(STARTED_AT, CURRENT_TIMESTAMP), LAST_ACTIVITY_AT = CURRENT_TIMESTAMP, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_PARTICIPACAO = ? AND STATUS <> 'RESPONDIDA'";
        return jdbc.update(sql, idParticipacao);
    }

    @Override
    public int touchActivity(Long idParticipacao) {
        final String sql = "UPDATE MEDIMETRIX.PARTICIPACAO SET LAST_ACTIVITY_AT = CURRENT_TIMESTAMP, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_PARTICIPACAO = ?";
        return jdbc.update(sql, idParticipacao);
    }

    @Override
    public int markSubmitted(Long idParticipacao) {
        final String sql = "UPDATE MEDIMETRIX.PARTICIPACAO SET STATUS = 'RESPONDIDA', SUBMITTED_AT = CURRENT_TIMESTAMP, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_PARTICIPACAO = ?";
        return jdbc.update(sql, idParticipacao);
    }

    @Override
    public int deleteById(Long idParticipacao) {
        final String sql = "DELETE FROM MEDIMETRIX.PARTICIPACAO WHERE ID_PARTICIPACAO = ?";
        return jdbc.update(sql, idParticipacao);
    }
}
