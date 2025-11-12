package com.mmx.medimetrix.infrastructure.dao.jdbc.especialidade;

import com.mmx.medimetrix.application.especialidade.port.out.EspecialidadeDao;
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

    private static final String BASE_SELECT = """
        SELECT ID_ESPECIALIDADE, NOME, ATIVO, DATA_CRIACAO, DATA_ULTIMA_EDICAO
        FROM MEDIMETRIX.ESPECIALIDADE
        """;

    @Override
    public Long insert(Especialidade e) {
        final String sql = """
            INSERT INTO MEDIMETRIX.ESPECIALIDADE (NOME, ATIVO)
            VALUES (?, COALESCE(?, TRUE))
            RETURNING ID_ESPECIALIDADE
            """;
        return jdbc.queryForObject(sql, Long.class, e.getNome(), e.getAtivo());
    }

    @Override
    public int update(Especialidade e) {
        final String sql = """
            UPDATE MEDIMETRIX.ESPECIALIDADE
               SET NOME = ?, ATIVO = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
             WHERE ID_ESPECIALIDADE = ?
            """;
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
        return jdbc.query(sql, MAPPER,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public List<Especialidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            ORDER BY NOME
            LIMIT ? OFFSET ?
            """;
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    // ===== IMPLEMENTAÇÕES NOVAS =====
    @Override
    public List<Especialidade> listByAtivoPaged(Boolean ativo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ?
            ORDER BY NOME
            LIMIT ? OFFSET ?
            """;
        return jdbc.query(sql, MAPPER,
                ativo != null ? ativo : Boolean.TRUE,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public List<Especialidade> searchByNomeAndAtivoLikePaged(String termo, Boolean ativo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ?
              AND UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            ORDER BY NOME
            LIMIT ? OFFSET ?
            """;
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                ativo != null ? ativo : Boolean.TRUE,
                pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    // ===== helper para ORDER BY seguro =====
    private String orderClause(String sortBy, boolean asc) {
        String col;
        if ("id".equalsIgnoreCase(sortBy))      col = "ID_ESPECIALIDADE";
        else /* nome como default */            col = "NOME";
        return " ORDER BY " + col + (asc ? " ASC " : " DESC ");
    }

    @Override
    public List<Especialidade> listPagedOrdered(String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER,
                limit != null ? limit : 50,
                offset != null ? offset : 0);
    }

    @Override
    public List<Especialidade> searchByNomeLikePagedOrdered(String termo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            """ + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0);
    }

    @Override
    public List<Especialidade> listByAtivoPagedOrdered(Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ?
            """ + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER,
                ativo != null ? ativo : Boolean.TRUE,
                limit != null ? limit : 50,
                offset != null ? offset : 0);
    }

    @Override
    public List<Especialidade> searchByNomeAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ?
              AND UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            """ + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                ativo != null ? ativo : Boolean.TRUE,
                pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0);
    }



    @Override
    public int deactivate(Long id) {
        final String sql = """
            UPDATE MEDIMETRIX.ESPECIALIDADE
               SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
             WHERE ID_ESPECIALIDADE = ?
            """;
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = """
            UPDATE MEDIMETRIX.ESPECIALIDADE
               SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
             WHERE ID_ESPECIALIDADE = ?
            """;
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.ESPECIALIDADE WHERE ID_ESPECIALIDADE = ?";
        return jdbc.update(sql, id);
    }
}
