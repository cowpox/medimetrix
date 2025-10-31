package com.mmx.medimetrix.infrastructure.dao.jdbc.meta;

import com.mmx.medimetrix.application.meta.port.out.MetaDao;
import com.mmx.medimetrix.domain.core.Meta;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class MetaDaoJdbc implements MetaDao {

    private final JdbcTemplate jdbc;
    private static final MetaRowMapper MAPPER = new MetaRowMapper();

    public MetaDaoJdbc(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    private static final String BASE_SELECT = "SELECT ID_META, ID_CRITERIO, ID_UNIDADE, ID_ESPECIALIDADE, ALVO, OPERADOR, VIGENCIA_INICIO, VIGENCIA_FIM, ATIVO, PRIORIDADE, JUSTIFICATIVA, DATA_CRIACAO, DATA_ULTIMA_EDICAO FROM MEDIMETRIX.META";

    @Override
    public Long insert(Meta m) {
        final String sql = "INSERT INTO MEDIMETRIX.META (ID_CRITERIO, ID_UNIDADE, ID_ESPECIALIDADE, ALVO, OPERADOR, VIGENCIA_INICIO, VIGENCIA_FIM, ATIVO, PRIORIDADE, JUSTIFICATIVA) VALUES (?, ?, ?, ?, COALESCE(?, '>='), ?, ?, COALESCE(?, TRUE), ?, ?) RETURNING ID_META";
        return jdbc.queryForObject(sql, Long.class,
                m.getIdCriterio(), m.getIdUnidade(), m.getIdEspecialidade(), m.getAlvo(), m.getOperador(),
                m.getVigenciaInicio(), m.getVigenciaFim(), m.getAtivo(), m.getPrioridade(), m.getJustificativa());
    }

    @Override
    public int update(Meta m) {
        final String sql = "UPDATE MEDIMETRIX.META SET ID_CRITERIO = ?, ID_UNIDADE = ?, ID_ESPECIALIDADE = ?, ALVO = ?, OPERADOR = ?, VIGENCIA_INICIO = ?, VIGENCIA_FIM = ?, ATIVO = ?, PRIORIDADE = ?, JUSTIFICATIVA = ?, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_META = ?";
        return jdbc.update(sql, m.getIdCriterio(), m.getIdUnidade(), m.getIdEspecialidade(), m.getAlvo(), m.getOperador(),
                m.getVigenciaInicio(), m.getVigenciaFim(), m.getAtivo(), m.getPrioridade(), m.getJustificativa(), m.getIdMeta());
    }

    @Override
    public Optional<Meta> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_META = ?";
        List<Meta> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Meta> listByCriterio(Long idCriterio, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_CRITERIO = ? ORDER BY VIGENCIA_INICIO DESC NULLS LAST, ID_META DESC LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idCriterio, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Meta> listByEscopo(Long idCriterio, Long idUnidade, Long idEspecialidade, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ID_CRITERIO = ? AND (ID_UNIDADE IS NOT DISTINCT FROM ?) AND (ID_ESPECIALIDADE IS NOT DISTINCT FROM ?) ORDER BY VIGENCIA_INICIO DESC NULLS LAST, ID_META DESC LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, idCriterio, idUnidade, idEspecialidade, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public List<Meta> listAtivasVigentesEm(LocalDate data, Integer offset, Integer limit) {
        final String sql = BASE_SELECT + " WHERE ATIVO = TRUE AND (VIGENCIA_INICIO IS NULL OR VIGENCIA_INICIO <= ?) AND (VIGENCIA_FIM IS NULL OR VIGENCIA_FIM >= ?) ORDER BY PRIORIDADE NULLS LAST, ID_META DESC LIMIT ? OFFSET ?";
        return jdbc.query(sql, MAPPER, data, data, limit != null ? limit : 50, offset != null ? offset : 0);
    }

    @Override
    public int deactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.META SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_META = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql = "UPDATE MEDIMETRIX.META SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP WHERE ID_META = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.META WHERE ID_META = ?";
        return jdbc.update(sql, id);
    }
}
