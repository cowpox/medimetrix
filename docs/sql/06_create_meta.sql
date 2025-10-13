-- =========================================
-- Script: 05_create_meta.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito na sessão:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabela CRITERIO já criada. UNIDADE/ESPECIALIDADE podem não existir ainda.

CREATE TABLE MEDIMETRIX.META (
                                 ID_META               BIGSERIAL,
                                 ID_CRITERIO           BIGINT  NOT NULL,
                                 ID_UNIDADE            BIGINT,             -- opcional
                                 ID_ESPECIALIDADE      BIGINT,             -- opcional

                                 ALVO                  DECIMAL(5,2) NOT NULL,     -- ex.: 4.20, 8.00
                                 OPERADOR              VARCHAR(2)  NOT NULL DEFAULT '>=',  -- '>=', '<=', '='

                                 VIGENCIA_INICIO       DATE,              -- opcional
                                 VIGENCIA_FIM          DATE,              -- opcional

                                 ATIVO                 BOOLEAN NOT NULL DEFAULT TRUE,
                                 PRIORIDADE            INT,               -- menor = mais prioritária
                                 FONTE                 VARCHAR(150),      -- opcional
                                 JUSTIFICATIVA         VARCHAR(1000),     -- opcional

                                 DATA_CRIACAO          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 DATA_ULTIMA_EDICAO    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                 CONSTRAINT "PK_META" PRIMARY KEY (ID_META),

                                 CONSTRAINT "FK_META_CRITERIO"
                                     FOREIGN KEY (ID_CRITERIO)
                                         REFERENCES MEDIMETRIX.CRITERIO(ID_CRITERIO)
                                         ON UPDATE RESTRICT ON DELETE RESTRICT,

    -- FKs de escopo (ativar quando UNIDADE e ESPECIALIDADE existirem)
    -- CONSTRAINT "FK_META_UNIDADE"
    --     FOREIGN KEY (ID_UNIDADE)
    --     REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
    --     ON UPDATE RESTRICT ON DELETE RESTRICT,
    --
    -- CONSTRAINT "FK_META_ESPECIALIDADE"
    --     FOREIGN KEY (ID_ESPECIALIDADE)
    --     REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
    --     ON UPDATE RESTRICT ON DELETE RESTRICT,

    -- Evita duplicata de início no mesmo escopo
                                 CONSTRAINT "UQ_META_INICIO_ESCOPO"
                                     UNIQUE (ID_CRITERIO, ID_UNIDADE, ID_ESPECIALIDADE, VIGENCIA_INICIO),

                                 CONSTRAINT "CK_META_OPERADOR"
                                     CHECK (OPERADOR IN ('>=','<=','=')),

                                 CONSTRAINT "CK_META_VIGENCIA"
                                     CHECK (VIGENCIA_INICIO IS NULL OR VIGENCIA_FIM IS NULL OR VIGENCIA_INICIO <= VIGENCIA_FIM)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_META_CRITERIO"      ON MEDIMETRIX.META(ID_CRITERIO);
CREATE INDEX IF NOT EXISTS "IDX_META_UNIDADE"       ON MEDIMETRIX.META(ID_UNIDADE);
CREATE INDEX IF NOT EXISTS "IDX_META_ESPECIALIDADE" ON MEDIMETRIX.META(ID_ESPECIALIDADE);
CREATE INDEX IF NOT EXISTS "IDX_META_ATIVO"         ON MEDIMETRIX.META(ATIVO);
CREATE INDEX IF NOT EXISTS "IDX_META_VIGENCIA_INI"  ON MEDIMETRIX.META(VIGENCIA_INICIO);

-- ==========================================================
-- (comentário) Prevenção robusta de sobreposição de vigência
-- Se o professor liberar recursos avançados, use EXCLUDE:
--   1) habilite extensões para operadores GiST de igualdade:
--      CREATE EXTENSION IF NOT EXISTS btree_gist;
--   2) crie o EXCLUDE para impedir intervalos sobrepostos
--      no mesmo escopo (apenas quando ATIVO=TRUE):
--
-- ALTER TABLE MEDIMETRIX.META
-- ADD CONSTRAINT "EX_META_OVERLAP"
-- EXCLUDE USING gist (
--   ID_CRITERIO WITH =,
--   COALESCE(ID_UNIDADE,        -1) WITH =,
--   COALESCE(ID_ESPECIALIDADE,  -1) WITH =,
--   daterange(VIGENCIA_INICIO, VIGENCIA_FIM, '[]') WITH &&
-- )
-- WHERE (ATIVO = TRUE);
--
-- Obs.: usar COALESCE(-1) trata metas "globais" (sem unidade/especialidade)
-- como um mesmo escopo no índice. Sem esse recurso, faça a checagem em trigger.
-- ==========================================================
