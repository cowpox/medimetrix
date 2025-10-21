-- ============================================================
-- V13__create_participacao.sql
-- Criação da tabela PARTICIPACAO (vínculo avaliação ↔ médico)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.PARTICIPACAO (
                                         ID_PARTICIPACAO            BIGSERIAL,
                                         ID_AVALIACAO               BIGINT NOT NULL,
                                         AVALIADO_MEDICO_ID         BIGINT NOT NULL,  -- FK para MEDICO (subtipo de USUARIO)

                                         UNIDADE_SNAPSHOT_ID        BIGINT,
                                         ESPECIALIDADE_SNAPSHOT_ID  BIGINT,

                                         STATUS                     VARCHAR(15) NOT NULL DEFAULT 'PENDENTE',  -- PENDENTE | EM_ANDAMENTO | RESPONDIDA
                                         STARTED_AT                 TIMESTAMP,
                                         LAST_ACTIVITY_AT           TIMESTAMP,
                                         SUBMITTED_AT               TIMESTAMP,

                                         DATA_CRIACAO               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         DATA_ULTIMA_EDICAO         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT PK_PARTICIPACAO PRIMARY KEY (ID_PARTICIPACAO),

                                         CONSTRAINT FK_PART_AVALIACAO
                                             FOREIGN KEY (ID_AVALIACAO)
                                                 REFERENCES MEDIMETRIX.AVALIACAO(ID_AVALIACAO)
                                                 ON UPDATE RESTRICT
                                                 ON DELETE RESTRICT,

    -- FKs ativadas posteriormente (V15 e V16)
    -- FK_PART_MEDICO → MEDICO(USUARIO_ID)
    -- FK_PART_UNIDADE_SNAP → UNIDADE(ID_UNIDADE)
    -- FK_PART_ESPEC_SNAP → ESPECIALIDADE(ID_ESPECIALIDADE)

                                         CONSTRAINT UQ_PART_AVAL_MEDICO UNIQUE (ID_AVALIACAO, AVALIADO_MEDICO_ID),

                                         CONSTRAINT CK_PART_STATUS
                                             CHECK (STATUS IN ('PENDENTE', 'EM_ANDAMENTO', 'RESPONDIDA')),

                                         CONSTRAINT CK_PART_SUBMITTED_STATUS
                                             CHECK (
                                                 (STATUS = 'RESPONDIDA' AND SUBMITTED_AT IS NOT NULL)
                                                     OR (STATUS <> 'RESPONDIDA' AND SUBMITTED_AT IS NULL)
                                                 )
);

CREATE INDEX IF NOT EXISTS IDX_PART_AVALIACAO   ON MEDIMETRIX.PARTICIPACAO(ID_AVALIACAO);
CREATE INDEX IF NOT EXISTS IDX_PART_MEDICO      ON MEDIMETRIX.PARTICIPACAO(AVALIADO_MEDICO_ID);
CREATE INDEX IF NOT EXISTS IDX_PART_STATUS      ON MEDIMETRIX.PARTICIPACAO(STATUS);
CREATE INDEX IF NOT EXISTS IDX_PART_UNID_SNAP   ON MEDIMETRIX.PARTICIPACAO(UNIDADE_SNAPSHOT_ID);
CREATE INDEX IF NOT EXISTS IDX_PART_ESPEC_SNAP  ON MEDIMETRIX.PARTICIPACAO(ESPECIALIDADE_SNAPSHOT_ID);
