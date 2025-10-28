package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.dao.MedicoDao;
import com.mmx.medimetrix.domain.core.Medico;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class MedicoDaoJdbc implements MedicoDao {

    private final JdbcTemplate jdbc;
    private static final MedicoRowMapper MAPPER = new MedicoRowMapper();

    public MedicoDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT USUARIO_ID, ESPECIALIDADE_ID, UNIDADE_ID, CRM_NUMERO, CRM_UF, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.MEDICO";

    @Override
    public Long insert(Medico m) {
        final String sql = "INSERT INTO MEDIMETRIX.MEDICO (USUARIO_ID, ESPECIALIDADE_ID, UNIDADE_ID, CRM_NUMERO, CRM_UF) VALUES (?, ?, ?, ?, ?) RETURNING USUARIO_ID";
        return jdbc.queryForObject(sql, Long.class, m.getUsuarioId(), m.getEspecialidadeId(), m.getUnidadeId(), m.getCrmNumero(), m.getCrmUf());
    }

    @Override
    public int update(Medico m) {
        final String sql = "UPDATE MEDIMETRIX.MEDICO SET ESPECIALIDADE_ID = ?, UNIDADE_ID = ?, CRM_NUMERO = ?, CRM_UF = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE USUARIO_ID = ?";
        return jdbc.update(sql, m.getEspecialidadeId(), m.getUnidadeId(), m.getCrmNumero(), m.getCrmUf(), m.getUsuarioId());
    }

    @Override
    public Optional<Medico> findByUsuarioId(Long usuarioId) {
        final String sql = BASE_SELECT + " WHERE USUARIO_ID = ?";
        List<Medico> list = jdbc.query(sql, MAPPER, usuarioId);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Medico> findByCrm(String crmNumero, String crmUf) {
        final String sql = BASE_SELECT + " WHERE CRM_NUMERO = ? AND CRM_UF = ?";
        List<Medico> list = jdbc.query(sql, MAPPER, crmNumero, crmUf);
        return list.stream().findFirst();
    }

    @Override
    public List<Medico> listByEspecialidade(Long especialidadeId, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ESPECIALIDADE_ID = ? ORDER BY USUARIO_ID LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, especialidadeId, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Medico> listByUnidade(Long unidadeId, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE UNIDADE_ID = ? ORDER BY USUARIO_ID LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, unidadeId, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Medico> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY USUARIO_ID LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deleteByUsuarioId(Long usuarioId) {
        final String sql = "DELETE FROM MEDIMETRIX.MEDICO WHERE USUARIO_ID = ?";
        return jdbc.update(sql, usuarioId);
    }
}
