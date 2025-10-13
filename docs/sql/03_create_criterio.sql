-- =========================================
-- Script: 01_create_criterio.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito na sessão:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.CRITERIO (
                                     ID_CRITERIO           BIGSERIAL,
                                     NOME                  VARCHAR(100)  NOT NULL,
                                     DEFINICAO_OPERACIONAL VARCHAR(500),         -- opcional
                                     DESCRICAO             VARCHAR(1000),        -- opcional

                                     ATIVO                 BOOLEAN NOT NULL DEFAULT TRUE,

                                     PESO                  DECIMAL(6,2) DEFAULT 1.00,  -- opcional; default 1.00
                                     ORDEM_SUGERIDA        INT,                        -- opcional

                                     VERSAO                INT NOT NULL DEFAULT 1,

                                     DATA_CRIACAO          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                     DATA_ULTIMA_EDICAO    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT "PK_CRITERIO" PRIMARY KEY (ID_CRITERIO),
                                     CONSTRAINT "UQ_CRITERIO_NOME" UNIQUE (NOME),
                                     CONSTRAINT "CK_CRITERIO_PESO_POSITIVO" CHECK (PESO IS NULL OR PESO > 0),
                                     CONSTRAINT "CK_CRITERIO_NOME_NAO_VAZIO" CHECK (LENGTH(TRIM(NOME)) > 0)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_CRITERIO_ATIVO" ON MEDIMETRIX.CRITERIO(ATIVO);
CREATE INDEX IF NOT EXISTS "IDX_CRITERIO_ORDEM" ON MEDIMETRIX.CRITERIO(ORDEM_SUGERIDA);

-- =========================================
-- Script: 02b_alter_questao_add_fk_criterio.sql
-- =========================================
ALTER TABLE MEDIMETRIX.QUESTAO
    ADD CONSTRAINT "FK_QUESTAO_CRITERIO"
        FOREIGN KEY (ID_CRITERIO)
            REFERENCES MEDIMETRIX.CRITERIO(ID_CRITERIO)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;
