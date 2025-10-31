package com.mmx.medimetrix.infrastructure.dao.jdbc.usuario;

import com.mmx.medimetrix.domain.core.Usuario;
import com.mmx.medimetrix.domain.enums.Papel;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioRowMapper implements RowMapper<Usuario> {

    @Override
    public Usuario mapRow(ResultSet rs, int rowNum) throws SQLException {
        Usuario u = new Usuario();
        u.setIdUsuario(rs.getLong("ID_USUARIO"));
        u.setNome(rs.getString("NOME"));
        u.setEmail(rs.getString("EMAIL"));
        u.setAtivo(rs.getBoolean("ATIVO"));
        String papel = rs.getString("PAPEL");
        u.setPapel(papel != null ? Papel.valueOf(papel) : null);
        u.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        u.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return u;
    }
}
