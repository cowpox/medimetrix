-- =========================================
-- Script: 04_create_avaliacao.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisito:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.AVALIACAO (
                                      ID_AVALIACAO          BIGSERIAL,
                                      TITULO                VARCHAR(120) NOT NULL,

                                      DATA_INICIO_APLIC     DATE NOT NULL,
                                      DATA_FIM_APLIC        DATE NOT NULL,

                                      K_MINIMO              INT  NOT NULL DEFAULT 3,
                                      STATUS                VARCHAR(12) NOT NULL,   -- RASCUNHO | PUBLICADA | ENCERRADA
                                      ATIVO                 BOOLEAN NOT NULL DEFAULT TRUE,

                                      VERSAO                INT NOT NULL DEFAULT 1,

                                      DATA_CRIACAO          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      DATA_ULTIMA_EDICAO    TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT "PK_AVALIACAO" PRIMARY KEY (ID_AVALIACAO),

                                      CONSTRAINT "UQ_AVALIACAO_TITULO_DTINI_APLIC"
                                          UNIQUE (TITULO, DATA_INICIO_APLIC),

                                      CONSTRAINT "CK_AVALIACAO_STATUS"
                                          CHECK (STATUS IN ('RASCUNHO','PUBLICADA','ENCERRADA')),

                                      CONSTRAINT "CK_AVALIACAO_KMIN"
                                          CHECK (K_MINIMO >= 2),

                                      CONSTRAINT "CK_AVALIACAO_DATAS"
                                          CHECK (DATA_INICIO_APLIC <= DATA_FIM_APLIC),

                                      CONSTRAINT "CK_AVALIACAO_TITULO_NAO_VAZIO"
                                          CHECK (LENGTH(TRIM(TITULO)) > 0)
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_STATUS"           ON MEDIMETRIX.AVALIACAO(STATUS);
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_DT_INICIO_APLIC"  ON MEDIMETRIX.AVALIACAO(DATA_INICIO_APLIC);
CREATE INDEX IF NOT EXISTS "IDX_AVALIACAO_ATIVO"            ON MEDIMETRIX.AVALIACAO(ATIVO);

-- (comentário) Campos de escopo (unidade/especialidade) foram removidos do MVP,
-- conforme MER atual. Se voltarem no futuro, criamos um script ALTER separado.
