-- =========================================
-- Script: 07_create_participacao.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito na sessão:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabela AVALIACAO já criada.
--   MEDICO/UNIDADE/ESPECIALIDADE podem não existir ainda (FKs comentadas).

CREATE TABLE MEDIMETRIX.PARTICIPACAO (
                                         ID_PARTICIPACAO              BIGSERIAL,

                                         ID_AVALIACAO                 BIGINT  NOT NULL,
                                         AVALIADO_MEDICO_ID           BIGINT  NOT NULL,     -- FK para MEDICO (ou USUARIO subtipo MEDICO)

                                         UNIDADE_SNAPSHOT_ID          BIGINT,               -- preenchido na criação (regra de negócio)
                                         ESPECIALIDADE_SNAPSHOT_ID    BIGINT,               -- preenchido na criação (regra de negócio)

                                         STATUS                       VARCHAR(15) NOT NULL DEFAULT 'PENDENTE',  -- PENDENTE | EM_ANDAMENTO | RESPONDIDA
                                         STARTED_AT                   TIMESTAMP,            -- primeiro acesso/gravação
                                         LAST_ACTIVITY_AT             TIMESTAMP,            -- última modificação
                                         SUBMITTED_AT                 TIMESTAMP,            -- momento da submissão (quando RESPONDIDA)

                                         DATA_CRIACAO                 TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         DATA_ULTIMA_EDICAO           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT "PK_PARTICIPACAO" PRIMARY KEY (ID_PARTICIPACAO),

                                         CONSTRAINT "FK_PART_AVALIACAO"
                                             FOREIGN KEY (ID_AVALIACAO)
                                                 REFERENCES MEDIMETRIX.AVALIACAO(ID_AVALIACAO)
                                                 ON UPDATE RESTRICT ON DELETE RESTRICT,

    -- Ativar estas FKs quando as tabelas existirem:
    -- CONSTRAINT "FK_PART_MEDICO"
    --     FOREIGN KEY (AVALIADO_MEDICO_ID)
    --     REFERENCES MEDIMETRIX.MEDICO(ID_MEDICO)        -- ou USUARIO(ID_USUARIO) se usar subtipo
    --     ON UPDATE RESTRICT ON DELETE RESTRICT,
    --
    -- CONSTRAINT "FK_PART_UNIDADE_SNAP"
    --     FOREIGN KEY (UNIDADE_SNAPSHOT_ID)
    --     REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
    --     ON UPDATE RESTRICT ON DELETE RESTRICT,
    --
    -- CONSTRAINT "FK_PART_ESPEC_SNAP"
    --     FOREIGN KEY (ESPECIALIDADE_SNAPSHOT_ID)
    --     REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
    --     ON UPDATE RESTRICT ON DELETE RESTRICT,

                                         CONSTRAINT "UQ_PART_AVAL_MEDICO"
                                             UNIQUE (ID_AVALIACAO, AVALIADO_MEDICO_ID),

                                         CONSTRAINT "CK_PART_STATUS"
                                             CHECK (STATUS IN ('PENDENTE','EM_ANDAMENTO','RESPONDIDA')),

                                         CONSTRAINT "CK_PART_SUBMITTED_STATUS"
                                             CHECK ((STATUS = 'RESPONDIDA' AND SUBMITTED_AT IS NOT NULL)
                                                 OR  (STATUS <> 'RESPONDIDA' AND SUBMITTED_AT IS NULL))
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_PART_AVALIACAO"   ON MEDIMETRIX.PARTICIPACAO(ID_AVALIACAO);
CREATE INDEX IF NOT EXISTS "IDX_PART_MEDICO"      ON MEDIMETRIX.PARTICIPACAO(AVALIADO_MEDICO_ID);
CREATE INDEX IF NOT EXISTS "IDX_PART_STATUS"      ON MEDIMETRIX.PARTICIPACAO(STATUS);
CREATE INDEX IF NOT EXISTS "IDX_PART_UNID_SNAP"   ON MEDIMETRIX.PARTICIPACAO(UNIDADE_SNAPSHOT_ID);
CREATE INDEX IF NOT EXISTS "IDX_PART_ESPEC_SNAP"  ON MEDIMETRIX.PARTICIPACAO(ESPECIALIDADE_SNAPSHOT_ID);
