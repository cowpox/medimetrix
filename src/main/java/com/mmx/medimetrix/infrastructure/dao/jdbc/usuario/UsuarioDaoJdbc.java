package com.mmx.medimetrix.infrastructure.dao.jdbc.usuario;

import com.mmx.medimetrix.application.usuario.port.out.UsuarioDao;
import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.domain.enums.Papel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioDaoJdbc implements UsuarioDao {

    private final JdbcTemplate jdbc;
    private static final UsuarioRowMapper MAPPER = new UsuarioRowMapper();

    public UsuarioDaoJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private static final String BASE_SELECT = "SELECT ID_USUARIO, NOME, EMAIL, ATIVO, PAPEL, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.USUARIO";

    @Override
    public Long insert(Usuario u) {
        final String sql = "INSERT INTO MEDIMETRIX.USUARIO (NOME, EMAIL, ATIVO, PAPEL) VALUES (?, ?, COALESCE(?, TRUE), ?) RETURNING ID_USUARIO";
        return jdbc.queryForObject(sql, Long.class,
                u.getNome(), u.getEmail(), u.getAtivo(), u.getPapel() != null ? u.getPapel().name() : null);
    }

    @Override
    public int update(Usuario u) {
        final String sql = "UPDATE MEDIMETRIX.USUARIO SET NOME = ?, EMAIL = ?, ATIVO = ?, PAPEL = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_USUARIO = ?";
        return jdbc.update(sql, u.getNome(), u.getEmail(), u.getAtivo(),
                u.getPapel() != null ? u.getPapel().name() : null, u.getIdUsuario());
    }

    @Override
    public List<Usuario> search(String termo, Papel papel, Boolean ativo,
                                int page, int size, String sort, boolean asc) {

        StringBuilder sql = new StringBuilder(BASE_SELECT + " WHERE 1=1 ");
        List<Object> params = new java.util.ArrayList<>();

        // termo -> NOME ou EMAIL (sem acento; se não usar UNACCENT, troque por ILIKE simples)
        if (termo != null && !termo.isBlank()) {
            sql.append(" AND (unaccent(NOME) ILIKE unaccent(?) OR unaccent(EMAIL) ILIKE unaccent(?)) ");
            String like = "%" + termo.trim() + "%";
            params.add(like);
            params.add(like);
        }

        if (papel != null) {
            sql.append(" AND PAPEL = ? ");
            params.add(papel.name());
        }

        if (ativo != null) {
            sql.append(" AND ATIVO = ? ");
            params.add(ativo);
        }

        // ORDER BY (whitelist)
        String sortCol;
        switch (sort != null ? sort.toUpperCase() : "NOME") {
            case "EMAIL"         -> sortCol = "EMAIL";
            case "PAPEL"         -> sortCol = "PAPEL";
            case "DATA_CRIACAO"  -> sortCol = "DATA_CRIACAO";
            case "ID", "ID_USUARIO" -> sortCol = "ID_USUARIO";
            case "NOME"          -> sortCol = "NOME";
            default              -> sortCol = "NOME";
        }
        sql.append(" ORDER BY ").append(sortCol).append(asc ? " ASC " : " DESC ");

        int limit  = Math.max(1, size);
        int offset = Math.max(0, page) * limit;
        sql.append(" LIMIT ? OFFSET ? ");
        params.add(limit);
        params.add(offset);

        return jdbc.query(sql.toString(), MAPPER, params.toArray());
    }




    @Override
    public Optional<Usuario> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_USUARIO = ?";
        List<Usuario> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Usuario> findByEmailCaseInsensitive(String email) {
        final String sql = BASE_SELECT + " WHERE LOWER(EMAIL) = LOWER(?)";
        List<Usuario> list = jdbc.query(sql, MAPPER, email);
        return list.stream().findFirst();
    }

    @Override
    public List<Usuario> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY ID_USUARIO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Usuario> listByPapelPaged(String papel, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE PAPEL = ? ORDER BY ID_USUARIO LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, papel, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.USUARIO SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_USUARIO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.USUARIO SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_USUARIO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.USUARIO WHERE ID_USUARIO = ?";
        return jdbc.update(sql, id);
    }
}
