-- =========================================
-- Script: 05_create_avaliacao_questao.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabelas AVALIACAO e QUESTAO já criadas.

CREATE TABLE MEDIMETRIX.AVALIACAO_QUESTAO (
                                              ID_AVALIACAO               BIGINT    NOT NULL,
                                              ID_QUESTAO                 BIGINT    NOT NULL,

                                              ORDEM                      INT       NOT NULL,
                                              OBRIGATORIA_NA_AVAL        BOOLEAN,                    -- NULL = herda da Questao
                                              ATIVA_NA_AVAL              BOOLEAN   NOT NULL DEFAULT TRUE,
                                              PESO_NA_AVAL               DECIMAL(6,2) DEFAULT 1.00,  -- opcional; default 1.00
                                              VISIVEL_PARA_GESTOR_LOCAL  BOOLEAN,                    -- NULL = herda da Questao
                                              OBSERVACAO_ADMIN           VARCHAR(200),               -- opcional

                                              DATA_CRIACAO               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                              DATA_ULTIMA_EDICAO         TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                              CONSTRAINT "PK_AVALIACAO_QUESTAO"
                                                  PRIMARY KEY (ID_AVALIACAO, ID_QUESTAO),

                                              CONSTRAINT "FK_AQ_AVALIACAO"
                                                  FOREIGN KEY (ID_AVALIACAO)
                                                      REFERENCES MEDIMETRIX.AVALIACAO(ID_AVALIACAO)
                                                      ON UPDATE RESTRICT ON DELETE RESTRICT,

                                              CONSTRAINT "FK_AQ_QUESTAO"
                                                  FOREIGN KEY (ID_QUESTAO)
                                                      REFERENCES MEDIMETRIX.QUESTAO(ID_QUESTAO)
                                                      ON UPDATE RESTRICT ON DELETE RESTRICT,

                                              CONSTRAINT "UQ_AQ_ORDEM_POR_AVAL"
                                                  UNIQUE (ID_AVALIACAO, ORDEM),

                                              CONSTRAINT "CK_AQ_ORDEM_POSITIVA"
                                                  CHECK (ORDEM >= 1),

                                              CONSTRAINT "CK_AQ_PESO_POSITIVO"
                                                  CHECK (PESO_NA_AVAL IS NULL OR PESO_NA_AVAL > 0)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_AQ_AVALIACAO" ON MEDIMETRIX.AVALIACAO_QUESTAO(ID_AVALIACAO);
CREATE INDEX IF NOT EXISTS "IDX_AQ_QUESTAO"   ON MEDIMETRIX.AVALIACAO_QUESTAO(ID_QUESTAO);
