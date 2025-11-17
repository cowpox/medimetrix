package com.mmx.medimetrix.infrastructure.dao.jdbc.avaliacao;

import com.mmx.medimetrix.application.avaliacao.port.out.AvaliacaoDao;
import com.mmx.medimetrix.domain.core.Avaliacao;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class AvaliacaoDaoJdbc implements AvaliacaoDao {

    private final JdbcTemplate jdbc;
    private static final AvaliacaoRowMapper MAPPER = new AvaliacaoRowMapper();

    public AvaliacaoDaoJdbc(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // IMPORTANTE: inclui ESCOPO, ID_UNIDADE, ID_ESPECIALIDADE no SELECT
    private static final String BASE_SELECT =
            "SELECT ID_AVALIACAO, TITULO, DATA_INICIO_APLIC, DATA_FIM_APLIC, " +
                    "K_MINIMO, STATUS, ATIVO, VERSAO, DATA_CRIACAO, DATA_ULTIMA_EDICAO, " +
                    "ESCOPO, ID_UNIDADE, ID_ESPECIALIDADE " +
                    "FROM MEDIMETRIX.AVALIACAO";

    @Override
    public Long insert(Avaliacao a) {
        // inclui ESCOPO, ID_UNIDADE, ID_ESPECIALIDADE no INSERT
        final String sql =
                "INSERT INTO MEDIMETRIX.AVALIACAO (" +
                        "TITULO, DATA_INICIO_APLIC, DATA_FIM_APLIC, " +
                        "K_MINIMO, STATUS, ATIVO, VERSAO, " +
                        "ESCOPO, ID_UNIDADE, ID_ESPECIALIDADE" +
                        ") VALUES (" +
                        "?, ?, ?, " +
                        "COALESCE(?, 3), ?, COALESCE(?, TRUE), COALESCE(?, 1), " +
                        "COALESCE(?, 'GLOBAL'), ?, ?" +
                        ") RETURNING ID_AVALIACAO";

        return jdbc.queryForObject(
                sql,
                Long.class,
                a.getTitulo(),
                a.getDataInicioAplic(),
                a.getDataFimAplic(),
                a.getkMinimo(),
                a.getStatus(),
                a.getAtivo(),
                a.getVersao(),
                a.getEscopo(),
                a.getIdUnidade(),
                a.getIdEspecialidade()
        );
    }

    @Override
    public int update(Avaliacao a) {
        // idem para UPDATE
        final String sql =
                "UPDATE MEDIMETRIX.AVALIACAO SET " +
                        "TITULO = ?, " +
                        "DATA_INICIO_APLIC = ?, " +
                        "DATA_FIM_APLIC = ?, " +
                        "K_MINIMO = ?, " +
                        "STATUS = ?, " +
                        "ATIVO = ?, " +
                        "VERSAO = ?, " +
                        "ESCOPO = ?, " +
                        "ID_UNIDADE = ?, " +
                        "ID_ESPECIALIDADE = ?, " +
                        "DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP " +
                        "WHERE ID_AVALIACAO = ?";

        return jdbc.update(
                sql,
                a.getTitulo(),
                a.getDataInicioAplic(),
                a.getDataFimAplic(),
                a.getkMinimo(),
                a.getStatus(),
                a.getAtivo(),
                a.getVersao(),
                a.getEscopo(),
                a.getIdUnidade(),
                a.getIdEspecialidade(),
                a.getIdAvaliacao()
        );
    }

    @Override
    public Optional<Avaliacao> findById(Long id) {
        final String sql = BASE_SELECT + " WHERE ID_AVALIACAO = ?";
        List<Avaliacao> list = jdbc.query(sql, MAPPER, id);
        return list.stream().findFirst();
    }

    @Override
    public List<Avaliacao> listByStatus(String status, Integer offset, Integer limit) {
        final String sql = BASE_SELECT +
                " WHERE STATUS = ? " +
                "ORDER BY DATA_INICIO_APLIC DESC, ID_AVALIACAO DESC " +
                "LIMIT ? OFFSET ?";
        return jdbc.query(
                sql,
                MAPPER,
                status,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public List<Avaliacao> listPaged(Integer offset, Integer limit) {
        final String sql = BASE_SELECT +
                " ORDER BY DATA_INICIO_APLIC DESC, ID_AVALIACAO DESC " +
                "LIMIT ? OFFSET ?";
        return jdbc.query(
                sql,
                MAPPER,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public List<Avaliacao> searchByTituloLikePaged(String termo, Integer offset, Integer limit) {
        final String sql = BASE_SELECT +
                " WHERE UNACCENT(LOWER(TITULO)) LIKE UNACCENT(LOWER(?)) " +
                "ORDER BY DATA_INICIO_APLIC DESC, ID_AVALIACAO DESC " +
                "LIMIT ? OFFSET ?";
        String pattern = "%" + (termo != null ? termo : "") + "%";
        return jdbc.query(
                sql,
                MAPPER,
                pattern,
                limit != null ? limit : 50,
                offset != null ? offset : 0
        );
    }

    @Override
    public int deactivate(Long id) {
        final String sql =
                "UPDATE MEDIMETRIX.AVALIACAO " +
                        "SET ATIVO = FALSE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP " +
                        "WHERE ID_AVALIACAO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int reactivate(Long id) {
        final String sql =
                "UPDATE MEDIMETRIX.AVALIACAO " +
                        "SET ATIVO = TRUE, DATA_ULTIMA_EDICAO = CURRENT_TIMESTAMP " +
                        "WHERE ID_AVALIACAO = ?";
        return jdbc.update(sql, id);
    }

    @Override
    public int deleteById(Long id) {
        final String sql = "DELETE FROM MEDIMETRIX.AVALIACAO WHERE ID_AVALIACAO = ?";
        return jdbc.update(sql, id);
    }
}
