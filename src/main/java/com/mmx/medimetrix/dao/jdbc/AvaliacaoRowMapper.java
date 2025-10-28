package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.domain.core.Avaliacao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AvaliacaoRowMapper implements RowMapper<Avaliacao> {
    @Override
    public Avaliacao mapRow(ResultSet rs, int rowNum) throws SQLException {
        Avaliacao a = new Avaliacao();
        a.setIdAvaliacao(rs.getLong("ID_AVALIACAO"));
        a.setTitulo(rs.getString("TITULO"));
        a.setDataInicioAplic(rs.getDate("DATA_INICIO_APLIC") != null ? rs.getDate("DATA_INICIO_APLIC").toLocalDate() : null);
        a.setDataFimAplic(rs.getDate("DATA_FIM_APLIC") != null ? rs.getDate("DATA_FIM_APLIC").toLocalDate() : null);
        a.setkMinimo(rs.getInt("K_MINIMO"));
        a.setStatus(rs.getString("STATUS"));
        a.setAtivo(rs.getBoolean("ATIVO"));
        a.setVersao(rs.getInt("VERSAO"));
        a.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        a.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return a;
    }
}
