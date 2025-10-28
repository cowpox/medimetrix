package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.dao.EspecialidadeDao;
import com.mmx.medimetrix.domain.core.Especialidade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EspecialidadeDaoJdbc implements EspecialidadeDao {

    private final JdbcTemplate jdbc;
    private static final EspecialidadeRowMapper MAPPER = new EspecialidadeRowMapper();

    public EspecialidadeDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_ESPECIALIDADE, NOME, ATIVO, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.ESPECIALIDADE";

    @Override
    public Long insert(Especialidade e) {
        final String sql = "INSERT INTO MEDIMETRIX.ESPECIALIDADE (NOME, ATIVO) VALUES (?, COALESCE(?, TRUE)) RETURNING ID_ESPECIALIDADE";
        return jdbc.queryForObject(sql, Long.class, e.getNome(), e.getAtivo());
    }

    @Override
    public int update(Especialidade e) {
        final String sql = "UPDATE MEDIMETRIX.ESPECIALIDADE SET NOME = ?, ATIVO = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_ESPECIALIDADE = ?";
        return jdbc.update(sql, e.getNome(), e.getAtivo(), e.getIdEspecialidade());
    }

    @Override
    public Optional<Especialidade> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_ESPECIALIDADE = ?";
        List<Especialidade> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Especialidade> listAllActive() {
        final String sql = BASE_SELECT + " WHERE ATIVO = TRUE ORDER BY NOME";
        return jdbc.query(sql, MAPPER);
    }

    @Override
    public List<Especialidade> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY ID_ESPECIALIDADE LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Especialidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?)) ORDER BY NOME LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.ESPECIALIDADE SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_ESPECIALIDADE = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.ESPECIALIDADE SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_ESPECIALIDADE = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";
        return jdbc.update(sql, id);
    }
}
