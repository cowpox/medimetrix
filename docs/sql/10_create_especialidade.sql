-- =========================================
-- Script: 09_create_especialidade.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.ESPECIALIDADE (
                                          ID_ESPECIALIDADE     BIGSERIAL,
                                          NOME                 VARCHAR(120) NOT NULL,
                                          ATIVO                BOOLEAN NOT NULL DEFAULT TRUE,

                                          DATA_CRIACAO         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                          DATA_ULTIMA_EDICAO   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                          CONSTRAINT "PK_ESPECIALIDADE" PRIMARY KEY (ID_ESPECIALIDADE),
                                          CONSTRAINT "UQ_ESPECIALIDADE_NOME" UNIQUE (NOME),
                                          CONSTRAINT "CK_ESPECIALIDADE_NOME_NAO_VAZIO" CHECK (LENGTH(TRIM(NOME)) > 0)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_ESPECIALIDADE_ATIVO" ON MEDIMETRIX.ESPECIALIDADE(ATIVO);

-- Ativar pendências
-- =========================================
-- Script: 10b_alter_fk_especialidade.sql
-- Pré-requisito: tabela MEDIMETRIX.ESPECIALIDADE criada
-- =========================================

-- AVALIACAO (escopo opcional)
ALTER TABLE MEDIMETRIX.AVALIACAO
    ADD CONSTRAINT "FK_AVALIACAO_ESPECIALIDADE"
        FOREIGN KEY (ESCOPO_ESPECIALIDADE_ID)
            REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;

-- META (escopo opcional)
ALTER TABLE MEDIMETRIX.META
    ADD CONSTRAINT "FK_META_ESPECIALIDADE"
        FOREIGN KEY (ID_ESPECIALIDADE)
            REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;

-- PARTICIPACAO (snapshot)
ALTER TABLE MEDIMETRIX.PARTICIPACAO
    ADD CONSTRAINT "FK_PART_ESPEC_SNAP"
        FOREIGN KEY (ESPECIALIDADE_SNAPSHOT_ID)
            REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;
