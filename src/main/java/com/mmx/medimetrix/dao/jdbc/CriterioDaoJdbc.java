package com.mmx.medimetrix.dao.jdbc;

import com.mmx.medimetrix.dao.CriterioDao;
import com.mmx.medimetrix.domain.core.Criterio;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CriterioDaoJdbc implements CriterioDao {

    private final JdbcTemplate jdbc;
    private static final CriterioRowMapper MAPPER = new CriterioRowMapper();

    public CriterioDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_CRITERIO, NOME, DEFINICAO_OPERACIONAL, DESCRICAO, ATIVO, PESO, ORDEM_SUGERIDA, VERSAO, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.CRITERIO";

    @Override
    public Long insert(Criterio c) {
        final String sql = "INSERT INTO MEDIMETRIX.CRITERIO (NOME, DEFINICAO_OPERACIONAL, DESCRICAO, ATIVO, PESO, ORDEM_SUGERIDA, VERSAO) VALUES (?, ?, ?, COALESCE(?, TRUE), COALESCE(?, 1.00), ?, COALESCE(?, 1)) RETURNING ID_CRITERIO";
        return jdbc.queryForObject(sql, Long.class,
                c.getNome(), c.getDefinicaoOperacional(), c.getDescricao(), c.getAtivo(), c.getPeso(), c.getOrdemSugerida(), c.getVersao());
    }

    @Override
    public int update(Criterio c) {
        final String sql = "UPDATE MEDIMETRIX.CRITERIO SET NOME = ?, DEFINICAO_OPERACIONAL = ?, DESCRICAO = ?, ATIVO = ?, PESO = ?, ORDEM_SUGERIDA = ?, VERSAO = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_CRITERIO = ?";
        return jdbc.update(sql, c.getNome(), c.getDefinicaoOperacional(), c.getDescricao(), c.getAtivo(), c.getPeso(), c.getOrdemSugerida(), c.getVersao(), c.getIdCriterio());
    }

    @Override
    public Optional<Criterio> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_CRITERIO = ?";
        List<Criterio> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public Optional<Criterio> findByNomeCaseInsensitive(String nome) {
        final String sql = BASE_SELECT + " WHERE LOWER(NOME) = LOWER(?)";
        List<Criterio> list = jdbc.query(sql, MAPPER, nome);
        return list.stream().findFirst();
    }

    @Override
    public List<Criterio> listAllActive() {
        final String sql = BASE_SELECT + " WHERE ATIVO = TRUE ORDER BY ORDEM_SUGERIDA NULLS LAST, NOME";
        return jdbc.query(sql, MAPPER);
    }

    @Override
    public List<Criterio> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " ORDER BY ORDEM_SUGERIDA NULLS LAST, NOME LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Criterio> searchByNomeLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE UNACCENT(LOWER(NOME)) LIKE UNACCENT(LOWER(?)) ORDER BY ORDEM_SUGERIDA NULLS LAST, NOME LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(sql, MAPPER, pattern, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.CRITERIO SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_CRITERIO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.CRITERIO SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_CRITERIO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.CRITERIO WHERE ID_CRITERIO = ?";
        return jdbc.update(sql, id);
    }
}
