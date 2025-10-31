package com.mmx.medimetrix.infrastructure.dao.jdbc.questao;

import com.mmx.medimetrix.domain.core.Questao;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuestaoRowMapper implements RowMapper<Questao> {
    @Override
    public Questao mapRow(ResultSet rs, int rowNum) throws SQLException {
        Questao q = new Questao();
        q.setIdQuestao(rs.getLong("ID_QUESTAO"));
        long crit = rs.getLong("ID_CRITERIO");
        q.setIdCriterio(rs.wasNull() ? null : crit);
        q.setEnunciado(rs.getString("ENUNCIADO"));
        q.setDescricaoAuxiliar(rs.getString("DESCRICAO_AUXILIAR"));
        q.setAtivo(rs.getBoolean("ATIVO"));
        q.setTipo(rs.getString("TIPO"));
        q.setObrigatoriedade(rs.getString("OBRIGATORIEDADE"));
        q.setValidacaoNumMin(rs.getBigDecimal("VALIDACAO_NUM_MIN"));
        q.setValidacaoNumMax(rs.getBigDecimal("VALIDACAO_NUM_MAX"));
        int tam = rs.getInt("TAMANHO_TEXTO_MAX");
        q.setTamanhoTextoMax(rs.wasNull() ? null : tam);
        q.setSensivel(rs.getBoolean("SENSIVEL"));
        q.setVisivelParaGestor(rs.getBoolean("VISIVEL_PARA_GESTOR"));
        int vers = rs.getInt("VERSAO");
        q.setVersao(rs.wasNull() ? null : vers);
        int ordem = rs.getInt("ORDEM_SUGERIDA");
        q.setOrdemSugerida(rs.wasNull() ? null : ordem);
        q.setDataCriacao(rs.getTimestamp("DATA_CRIACAO") != null ? rs.getTimestamp("DATA_CRIACAO").toLocalDateTime() : null);
        q.setDataUltimaEdicao(rs.getTimestamp("DATA_ULTIMA_EDICAO") != null ? rs.getTimestamp("DATA_ULTIMA_EDICAO").toLocalDateTime() : null);
        return q;
    }
}
