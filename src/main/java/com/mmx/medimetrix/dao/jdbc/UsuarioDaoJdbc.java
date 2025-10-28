package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.dao.UsuarioDao;
import com.mmx.medimetrix.domain.core.Usuario;
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
