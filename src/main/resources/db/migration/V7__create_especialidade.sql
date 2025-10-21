-- ============================================================
-- V7__create_especialidade.sql
-- Criação da tabela ESPECIALIDADE
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.ESPECIALIDADE (
                                          ID_ESPECIALIDADE   BIGSERIAL,
                                          NOME               VARCHAR(120) NOT NULL,
                                          ATIVO              BOOLEAN NOT NULL DEFAULT TRUE,

                                          DATA_CRIACAO       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT PK_ESPECIALIDADE PRIMARY KEY (ID_ESPECIALIDADE),
                                          CONSTRAINT CK_ESPECIALIDADE_NOME CHECK (LENGTH(TRIM(NOME)) > 0)
);

CREATE INDEX IF NOT EXISTS IDX_ESPECIALIDADE_ATIVO ON MEDIMETRIX.ESPECIALIDADE(ATIVO);
