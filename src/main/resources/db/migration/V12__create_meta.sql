-- ============================================================
-- V12__create_meta.sql
-- Criação da tabela META (metas por critério e escopo)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.META (
                                 ID_META             BIGSERIAL,
                                 ID_CRITERIO         BIGINT  NOT NULL,
                                 ID_UNIDADE          BIGINT,            -- FK adicionada no V15
                                 ID_ESPECIALIDADE    BIGINT,            -- FK adicionada no V15

                                 ALVO                NUMERIC(5,2) NOT NULL,       -- ex.: 4.20, 8.00
                                 OPERADOR            VARCHAR(2)  NOT NULL DEFAULT '>=',  -- '>=', '<=', '='

                                 VIGENCIA_INICIO     DATE,
                                 VIGENCIA_FIM        DATE,

                                 ATIVO               BOOLEAN NOT NULL DEFAULT TRUE,
                                 PRIORIDADE          INT,
                                 JUSTIFICATIVA       VARCHAR(1000),

                                 DATA_CRIACAO        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 DATA_ULTIMA_EDICAO  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT PK_META PRIMARY KEY (ID_META),

                                 CONSTRAINT FK_META_CRITERIO
                                     FOREIGN KEY (ID_CRITERIO)
                                         REFERENCES MEDIMETRIX.CRITERIO(ID_CRITERIO)
                                         ON UPDATE RESTRICT
                                         ON DELETE RESTRICT,

                                 CONSTRAINT UQ_META_INICIO_ESCOPO
                                     UNIQUE (ID_CRITERIO, ID_UNIDADE, ID_ESPECIALIDADE, VIGENCIA_INICIO),

                                 CONSTRAINT CK_META_OPERADOR
                                     CHECK (OPERADOR IN ('>=','<=','=')),

                                 CONSTRAINT CK_META_VIGENCIA
                                     CHECK (
                                         VIGENCIA_INICIO IS NULL
                                             OR VIGENCIA_FIM IS NULL
                                             OR VIGENCIA_INICIO <= VIGENCIA_FIM
                                         )
);

CREATE INDEX IF NOT EXISTS IDX_META_CRITERIO       ON MEDIMETRIX.META(ID_CRITERIO);
CREATE INDEX IF NOT EXISTS IDX_META_UNIDADE        ON MEDIMETRIX.META(ID_UNIDADE);
CREATE INDEX IF NOT EXISTS IDX_META_ESPECIALIDADE  ON MEDIMETRIX.META(ID_ESPECIALIDADE);
CREATE INDEX IF NOT EXISTS IDX_META_ATIVO          ON MEDIMETRIX.META(ATIVO);
CREATE INDEX IF NOT EXISTS IDX_META_VIGENCIA_INI   ON MEDIMETRIX.META(VIGENCIA_INICIO);
