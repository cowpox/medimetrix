-- =========================================
-- Script: 03_create_avaliacao.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito na sessão:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.AVALIACAO (
                                      ID_AVALIACAO        BIGSERIAL,
                                      TITULO              VARCHAR(120) NOT NULL,
                                      DESCRICAO           VARCHAR(1000),          -- opcional

                                      DT_INICIO           DATE NOT NULL,
                                      DT_FIM              DATE NOT NULL,

                                      K_MINIMO            INT  NOT NULL DEFAULT 3,
                                      STATUS              VARCHAR(12) NOT NULL,   -- RASCUNHO | PUBLICADA | ENCERRADA
                                      ATIVO               BOOLEAN NOT NULL DEFAULT TRUE,

                                      VERSAO              INT NOT NULL DEFAULT 1,

                                      DATA_CRIACAO        TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      DATA_ULTIMA_EDICAO  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      ESCOPO_UNIDADE_ID         BIGINT,           -- FK comentada (ativaremos quando UNIDADE existir)
                                      ESCOPO_ESPECIALIDADE_ID   BIGINT,           -- FK comentada (ativaremos quando ESPECIALIDADE existir)

                                      CONSTRAINT "PK_AVALIACAO" PRIMARY KEY (ID_AVALIACAO),

                                      CONSTRAINT "UQ_AVALIACAO_TITULO_DTINI" UNIQUE (TITULO, DT_INICIO),

                                      CONSTRAINT "CK_AVALIACAO_STATUS"
                                          CHECK (STATUS IN ('RASCUNHO','PUBLICADA','ENCERRADA')),

                                      CONSTRAINT "CK_AVALIACAO_KMIN"
                                          CHECK (K_MINIMO >= 2),

                                      CONSTRAINT "CK_AVALIACAO_DATAS"
                                          CHECK (DT_INICIO <= DT_FIM),

                                      CONSTRAINT "CK_AVALIACAO_TITULO_NAO_VAZIO"
                                          CHECK (LENGTH(TRIM(TITULO)) > 0)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_STATUS" ON MEDIMETRIX.AVALIACAO(STATUS);
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_DT_INICIO" ON MEDIMETRIX.AVALIACAO(DT_INICIO);
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_ATIVO" ON MEDIMETRIX.AVALIACAO(ATIVO);

-- FKs de ESCOPO (ativar quando criarmos UNIDADE e ESPECIALIDADE)
-- ALTER TABLE MEDIMETRIX.AVALIACAO
--   ADD CONSTRAINT "FK_AVALIACAO_UNIDADE"
--   FOREIGN KEY (ESCOPO_UNIDADE_ID)
--   REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
--   ON UPDATE RESTRICT ON DELETE RESTRICT;
--
-- ALTER TABLE MEDIMETRIX.AVALIACAO
--   ADD CONSTRAINT "FK_AVALIACAO_ESPECIALIDADE"
--   FOREIGN KEY (ESCOPO_ESPECIALIDADE_ID)
--   REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
--   ON UPDATE RESTRICT ON DELETE RESTRICT;
