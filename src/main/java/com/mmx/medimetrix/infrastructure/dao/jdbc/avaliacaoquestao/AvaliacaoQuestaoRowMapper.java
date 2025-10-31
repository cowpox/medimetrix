package com.mmx.medimetrix.infrastructure.dao.jdbc.avaliacaoquestao;

import com.mmx.medimetrix.domain.core.AvaliacaoQuestao;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AvaliacaoQuestaoRowMapper implements RowMapper<AvaliacaoQuestao> {
    @Override
    public AvaliacaoQuestao mapRow(ResultSet rs, int rowNum) throws SQLException {
        AvaliacaoQuestao aq = new AvaliacaoQuestao();
        aq.setIdAvaliacao(rs.getLong("ID_AVALIACAO"));
        aq.setIdQuestao(rs.getLong("ID_QUESTAO"));
        aq.setOrdem(rs.getInt("ORDEM"));
        aq.setObrigatoriaNaAval((Boolean) rs.getObject("OBRIGATORIA_NA_AVAL"));
        aq.setAtivaNaAval(rs.getBoolean("ATIVA_NA_AVAL"));
        aq.setPesoNaAval(rs.getBigDecimal("PESO_NA_AVAL"));
        aq.setVisivelParaGestorLocal((Boolean) rs.getObject("VISIVEL_PARA_GESTOR_LOCAL"));
        aq.setObservacaoAdmin(rs.getString("OBSERVACAO_ADMIN"));
        aq.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        aq.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return aq;
    }
}
