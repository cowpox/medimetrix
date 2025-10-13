-- =========================================
-- Script: 02_create_questao.sql
-- Projeto: MediMetrix
-- Banco: medimetrix_db (PostgreSQL)
-- Schema ativo: MEDIMETRIX
-- Estilo compatível com material de aula (nomes em MAIÚSCULO, constraints nomeadas)
-- =========================================

-- Pré-requisito: estar conectado a medimetrix_db e com search_path para MEDIMETRIX
-- Exemplo (psql):
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

-- =========================================
-- Tabela: QUESTAO  (catálogo de itens)
-- =========================================
CREATE TABLE MEDIMETRIX.QUESTAO (
                                    ID_QUESTAO           BIGSERIAL,
                                    ID_CRITERIO          BIGINT NOT NULL,                 -- FK adicionada depois de criarmos CRITERIO
                                    ENUNCIADO            VARCHAR(1000) NOT NULL,
                                    DESCRICAO_AUXILIAR   VARCHAR(500),

                                    ATIVO                BOOLEAN NOT NULL DEFAULT TRUE,

                                    TIPO                 VARCHAR(20) NOT NULL,            -- LIKERT_5 | BARS_5 | OPEN | CHECK | NUM_0_10
                                    OBRIGATORIEDADE      VARCHAR(12) NOT NULL DEFAULT 'OBRIGATORIA',  -- OPCIONAL | OBRIGATORIA

                                    VALIDACAO_NUM_MIN    NUMERIC(10,3),
                                    VALIDACAO_NUM_MAX    NUMERIC(10,3),

                                    TAMANHO_TEXTO_MAX    INT,

                                    CONFIG_JSON          JSONB,  -- (comentário) JSONB é recurso do PostgreSQL; incluído para evitar inconsistências futuras

                                    SENSIVEL             BOOLEAN NOT NULL DEFAULT FALSE,
                                    VISIVEL_PARA_GESTOR  BOOLEAN NOT NULL DEFAULT TRUE,

                                    VERSAO               INT NOT NULL DEFAULT 1,
                                    ORDEM_SUGERIDA       INT,

                                    DATA_CRIACAO         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    DATA_ULTIMA_EDICAO   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT "PK_QUESTAO" PRIMARY KEY (ID_QUESTAO),

    -- Domínios básicos
                                    CONSTRAINT "CK_QUESTAO_TIPO"
                                        CHECK (TIPO IN ('LIKERT_5','BARS_5','OPEN','CHECK','NUM_0_10')),

                                    CONSTRAINT "CK_QUESTAO_OBRIGATORIEDADE"
                                        CHECK (OBRIGATORIEDADE IN ('OPCIONAL','OBRIGATORIA')),

    -- Regras mínimas de coerência (necessárias para evitar inconsistências comuns)
                                    CONSTRAINT "CK_QUESTAO_LIMITES_POR_TIPO"
                                        CHECK (
                                            (TIPO IN ('LIKERT_5','BARS_5') AND VALIDACAO_NUM_MIN = 1 AND VALIDACAO_NUM_MAX = 5)
                                                OR (TIPO = 'NUM_0_10'            AND VALIDACAO_NUM_MIN = 0 AND VALIDACAO_NUM_MAX = 10)
                                                OR (TIPO IN ('OPEN','CHECK')     AND VALIDACAO_NUM_MIN IS NULL AND VALIDACAO_NUM_MAX IS NULL)
                                            ),

                                    CONSTRAINT "CK_QUESTAO_TEXTO_POR_TIPO"
                                        CHECK (
                                            (TIPO = 'OPEN'  AND TAMANHO_TEXTO_MAX IS NOT NULL AND TAMANHO_TEXTO_MAX > 0)
                                                OR (TIPO <> 'OPEN' AND TAMANHO_TEXTO_MAX IS NULL)
                                            ),

    -- Enunciado não pode ser só espaços
                                    CONSTRAINT "CK_QUESTAO_ENUNCIADO"
                                        CHECK (LENGTH(TRIM(ENUNCIADO)) > 0)
);

-- Índices básicos
CREATE INDEX IF NOT EXISTS "IDX_QUESTAO_CRITERIO" ON MEDIMETRIX.QUESTAO(ID_CRITERIO);
CREATE INDEX IF NOT EXISTS "IDX_QUESTAO_TIPO"     ON MEDIMETRIX.QUESTAO(TIPO);
CREATE INDEX IF NOT EXISTS "IDX_QUESTAO_ATIVO"    ON MEDIMETRIX.QUESTAO(ATIVO);

-- FK para CRITERIO (executar após criar CRITERIO)
-- ALTER TABLE MEDIMETRIX.QUESTAO
--   ADD CONSTRAINT "FK_QUESTAO_CRITERIO"
--   FOREIGN KEY (ID_CRITERIO)
--   REFERENCES MEDIMETRIX.CRITERIO(ID_CRITERIO)
--   ON UPDATE RESTRICT ON DELETE RESTRICT;
