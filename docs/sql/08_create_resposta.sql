-- =========================================
-- Script: 08_create_resposta.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabelas AVALIACAO, QUESTAO, AVALIACAO_QUESTAO e PARTICIPACAO já criadas.

CREATE TABLE MEDIMETRIX.RESPOSTA (
                                     ID_RESPOSTA       BIGSERIAL,

                                     ID_PARTICIPACAO   BIGINT  NOT NULL,
                                     ID_AVALIACAO      BIGINT  NOT NULL,
                                     ID_QUESTAO        BIGINT  NOT NULL,

                                     DATA_RESPOSTA     TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     VALOR_NUMERICO    NUMERIC(6,3),
                                     VALOR_BINARIO     SMALLINT,          -- 0 ou 1 (checagem fica para regra de aplicação por enquanto)
                                     TEXTO             VARCHAR(2000),

                                     CONSTRAINT "PK_RESPOSTA" PRIMARY KEY (ID_RESPOSTA),

                                     CONSTRAINT "FK_RESP_PARTICIPACAO"
                                         FOREIGN KEY (ID_PARTICIPACAO)
                                             REFERENCES MEDIMETRIX.PARTICIPACAO(ID_PARTICIPACAO)
                                             ON UPDATE RESTRICT ON DELETE RESTRICT,

                                     CONSTRAINT "FK_RESP_AQ_AVAL"
                                         FOREIGN KEY (ID_AVALIACAO, ID_QUESTAO)
                                             REFERENCES MEDIMETRIX.AVALIACAO_QUESTAO(ID_AVALIACAO, ID_QUESTAO)
                                             ON UPDATE RESTRICT ON DELETE RESTRICT,

    -- 1 resposta por (participacao, questao)
                                     CONSTRAINT "UQ_RESP_PART_QUESTAO"
                                         UNIQUE (ID_PARTICIPACAO, ID_QUESTAO),

    -- Regras gerais mínimas (sem olhar o tipo ainda)
                                     CONSTRAINT "CK_RESP_PELO_MENOS_UM_VALOR"
                                         CHECK (VALOR_NUMERICO IS NOT NULL OR VALOR_BINARIO IS NOT NULL OR TEXTO IS NOT NULL),

                                     CONSTRAINT "CK_RESP_NAO_MISTURAR_NUM_E_BIN"
                                         CHECK (NOT (VALOR_NUMERICO IS NOT NULL AND VALOR_BINARIO IS NOT NULL))
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_RESP_PART"   ON MEDIMETRIX.RESPOSTA(ID_PARTICIPACAO);
CREATE INDEX IF NOT EXISTS "IDX_RESP_AVAL"   ON MEDIMETRIX.RESPOSTA(ID_AVALIACAO);
CREATE INDEX IF NOT EXISTS "IDX_RESP_QUEST"  ON MEDIMETRIX.RESPOSTA(ID_QUESTAO);

-- (comentário) Regras detalhadas por tipo (LIKERT/BARS/OPEN/CHECK/NUM)
-- serão implementadas depois com TRIGGER, seguindo a evolução da matéria.
