package com.mmx.medimetrix.infrastructure.dao.jdbc.unidade;

import com.mmx.medimetrix.application.unidade.port.out.UnidadeDao;
import com.mmx.medimetrix.domain.core.Unidade;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UnidadeDaoJdbc implements UnidadeDao {

    private final JdbcTemplate jdbc;
    private static final UnidadeRowMapper MAPPER = new UnidadeRowMapper();

    public UnidadeDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = """
        SELECT ID_UNIDADE, NOME, GESTOR_USUARIO_ID, ATIVO, DATA_CRIACAO, DATA_ULTIMA_EDICAO
          FROM MEDIMETRIX.UNIDADE
        """;

    private String orderClause(String sortBy, boolean asc) {
        String col;
        if ("id".equalsIgnoreCase(sortBy))      col = "ID_UNIDADE";
        else /* nome como default */            col = "NOME";
        return " ORDER BY " + col + (asc ? " ASC " : " DESC ");
    }

    @Override
    public Long insert(Unidade u) {
        final String sql = """
            INSERT INTO MEDIMETRIX.UNIDADE (NOME, GESTOR_USUARIO_ID, ATIVO)
            VALUES (?, ?, COALESCE(?, TRUE))
            RETURNING ID_UNIDADE
            """;
        return jdbc.queryForObject(sql, Long.class, u.getNome(), u.getGestorUsuarioId(), u.getAtivo());
    }

    @Override
    public int update(Unidade u) {
        final String sql = """
            UPDATE MEDIMETRIX.UNIDADE
               SET NOME = ?, GESTOR_USUARIO_ID = ?, ATIVO = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP
             WHERE ID_UNIDADE = ?
            """;
        return jdbc.update(sql, u.getNome(), u.getGestorUsuarioId(), u.getAtivo(), u.getIdUnidade());
    }

    @Override
    public Optional<Unidade> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_UNIDADE = ?";
        var list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Unidade> listAllActive() {
        final String sql = BASE_SELECT + " WHERE ATIVO = TRUE ORDER BY NOME";
        return jdbc.query(sql, MAPPER);
    }

    @Override
    public List<Unidade> listByGestor(Long gestorUsuarioId) {
        final String sql = BASE_SELECT + " WHERE GESTOR_USUARIO_ID = ? ORDER BY NOME";
        return jdbc.query(sql, MAPPER, gestorUsuarioId);
    }

    // existentes (mantidos)
    @Override
    public List<Unidade> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY ID_UNIDADE LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Unidade> searchByNomeLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            ORDER BY NOME
            LIMIT ? OFFSET ?
            """;
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    // novos: filtro por ativo
    @Override
    public List<Unidade> listByAtivoPaged(Boolean ativo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ATIVO = ? ORDER BY NOME LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, ativo != null ? ativo : Boolean.TRUE, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Unidade> searchByNomeAndAtivoLikePaged(String termo, Boolean ativo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ? AND UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            ORDER BY NOME
            LIMIT ? OFFSET ?
            """;
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, ativo != null ? ativo : Boolean.TRUE, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    // novos: ordered
    @Override
    public List<Unidade> listPagedOrdered(String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Unidade> searchByNomeLikePagedOrdered(String termo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            """ + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Unidade> searchByNomeOuGestorLikePagedOrdered(String termo, String sortBy, boolean asc,
                                                              Integer offset, Integer limit) {
        // sortBy whitelist (reaproveitando sua helper orderClause)
        String order = orderClause(sortBy, asc);

        final String sql = """
        SELECT U.ID_UNIDADE, U.NOME, U.GESTOR_USUARIO_ID, U.ATIVO, U.DATA_CRIACAO, U.DATA_ULTIMA_EDICAO
          FROM MEDIMETRIX.UNIDADE U
          LEFT JOIN MEDIMETRIX.USUARIO G ON G.ID_USUARIO = U.GESTOR_USUARIO_ID
         WHERE UNACCENT(LOWER(U.NOME)) LIKE UNACCENT(LOWER(?))
            OR UNACCENT(LOWER(G.NOME)) LIKE UNACCENT(LOWER(?))
        """ + order + "LIMIT ? OFFSET ?";

        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                pattern, pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public List<Unidade> searchByNomeOuGestorAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc,
                                                                      Integer offset, Integer limit) {
        String order = orderClause(sortBy, asc);

        final String sql = """
        SELECT U.ID_UNIDADE, U.NOME, U.GESTOR_USUARIO_ID, U.ATIVO, U.DATA_CRIACAO, U.DATA_ULTIMA_EDICAO
          FROM MEDIMETRIX.UNIDADE U
          LEFT JOIN MEDIMETRIX.USUARIO G ON G.ID_USUARIO = U.GESTOR_USUARIO_ID
         WHERE U.ATIVO = ?
           AND ( UNACCENT(LOWER(U.NOME)) LIKE UNACCENT(LOWER(?))
              OR UNACCENT(LOWER(G.NOME)) LIKE UNACCENT(LOWER(?)) )
        """ + order + "LIMIT ? OFFSET ?";

        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER,
                (ativo != null ? ativo : Boolean.TRUE),
                pattern, pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }




    @Override
    public List<Unidade> listByAtivoPagedOrdered(Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ATIVO = ?" + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, ativo != null ? ativo : Boolean.TRUE, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Unidade> searchByNomeAndAtivoLikePagedOrdered(String termo, Boolean ativo, String sortBy, boolean asc, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + """
            WHERE ATIVO = ? AND UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?))
            """ + orderClause(sortBy, asc) + "LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, ativo != null ? ativo : Boolean.TRUE, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.UNIDADE SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_UNIDADE = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.UNIDADE SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_UNIDADE = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.UNIDADE WHERE ID_UNIDADE = ?";
        return jdbc.update(sql, id);
    }
}
