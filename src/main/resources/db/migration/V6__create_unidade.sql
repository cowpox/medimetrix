-- ============================================================
-- V6__create_unidade.sql
-- Criação da tabela UNIDADE
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.UNIDADE (
                                    ID_UNIDADE         BIGSERIAL,
                                    NOME               VARCHAR(120) NOT NULL,
                                    GESTOR_USUARIO_ID  BIGINT NOT NULL,  -- FK para USUARIO será criada no V8
                                    ATIVO              BOOLEAN NOT NULL DEFAULT TRUE,

                                    DATA_CRIACAO       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT PK_UNIDADE PRIMARY KEY (ID_UNIDADE),
                                    CONSTRAINT CK_UNIDADE_NOME CHECK (LENGTH(TRIM(NOME)) > 0)
);

CREATE INDEX IF NOT EXISTS IDX_UNIDADE_ATIVO ON MEDIMETRIX.UNIDADE(ATIVO);
