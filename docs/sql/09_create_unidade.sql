-- =========================================
-- Script: 09_create_unidade.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.UNIDADE (
                                    ID_UNIDADE         BIGSERIAL,
                                    NOME               VARCHAR(120) NOT NULL,
                                    GESTOR_USUARIO_ID  BIGINT      NOT NULL,  -- gestor obrigatório
                                    ATIVO              BOOLEAN     NOT NULL DEFAULT TRUE,

                                    DATA_CRIACAO       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    DATA_ULTIMA_EDICAO TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT "PK_UNIDADE" PRIMARY KEY (ID_UNIDADE),
                                    CONSTRAINT "UQ_UNIDADE_NOME" UNIQUE (NOME),
                                    CONSTRAINT "CK_UNIDADE_NOME_NAO_VAZIO" CHECK (LENGTH(TRIM(NOME)) > 0)

    -- FK será ativada quando criarmos USUARIO/GESTOR:
    -- ,CONSTRAINT "FK_UNIDADE_GESTOR"
    --   FOREIGN KEY (GESTOR_USUARIO_ID)
    --   REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
    --   ON UPDATE RESTRICT ON DELETE RESTRICT
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_UNIDADE_GESTOR" ON MEDIMETRIX.UNIDADE(GESTOR_USUARIO_ID);
CREATE INDEX IF NOT EXISTS "IDX_UNIDADE_ATIVO"  ON MEDIMETRIX.UNIDADE(ATIVO);

-- Ativar pendências
-- AVALIACAO → UNIDADE (campo de escopo)
ALTER TABLE MEDIMETRIX.AVALIACAO
    ADD CONSTRAINT "FK_AVALIACAO_UNIDADE"
        FOREIGN KEY (ESCOPO_UNIDADE_ID)
            REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;

-- META → UNIDADE (escopo opcional)
ALTER TABLE MEDIMETRIX.META
    ADD CONSTRAINT "FK_META_UNIDADE"
        FOREIGN KEY (ID_UNIDADE)
            REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;

-- PARTICIPACAO → UNIDADE (snapshot)
ALTER TABLE MEDIMETRIX.PARTICIPACAO
    ADD CONSTRAINT "FK_PART_UNIDADE_SNAP"
        FOREIGN KEY (UNIDADE_SNAPSHOT_ID)
            REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
            ON UPDATE RESTRICT ON DELETE RESTRICT;
