-- ============================================================
-- V14__create_resposta.sql
-- Criação da tabela RESPOSTA (valores por questão/participação)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.RESPOSTA (
                                     ID_RESPOSTA       BIGSERIAL,

                                     ID_PARTICIPACAO   BIGINT NOT NULL,
                                     ID_AVALIACAO      BIGINT NOT NULL,
                                     ID_QUESTAO        BIGINT NOT NULL,

    -- valores possíveis por tipo de questão
                                     VALOR_NUMERICO    NUMERIC(10,3),
                                     VALOR_BINARIO     BOOLEAN,
                                     TEXTO             VARCHAR(2000),

                                     DATA_CRIACAO      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                     CONSTRAINT PK_RESPOSTA PRIMARY KEY (ID_RESPOSTA),

                                     CONSTRAINT FK_RESPOSTA_PART
                                         FOREIGN KEY (ID_PARTICIPACAO)
                                             REFERENCES MEDIMETRIX.PARTICIPACAO(ID_PARTICIPACAO)
                                             ON UPDATE RESTRICT
                                             ON DELETE CASCADE,

                                     CONSTRAINT FK_RESPOSTA_AVAL
                                         FOREIGN KEY (ID_AVALIACAO)
                                             REFERENCES MEDIMETRIX.AVALIACAO(ID_AVALIACAO)
                                             ON UPDATE RESTRICT
                                             ON DELETE RESTRICT,

                                     CONSTRAINT FK_RESPOSTA_QUEST
                                         FOREIGN KEY (ID_QUESTAO)
                                             REFERENCES MEDIMETRIX.QUESTAO(ID_QUESTAO)
                                             ON UPDATE RESTRICT
                                             ON DELETE RESTRICT,

    -- Pelo menos um valor precisa estar presente
                                     CONSTRAINT CK_RESP_PELO_MENOS_UM_VALOR
                                         CHECK (VALOR_NUMERICO IS NOT NULL OR VALOR_BINARIO IS NOT NULL OR TEXTO IS NOT NULL),

    -- Não pode misturar numérico e binário
                                     CONSTRAINT CK_RESP_NAO_MISTURAR_NUM_E_BIN
                                         CHECK (NOT (VALOR_NUMERICO IS NOT NULL AND VALOR_BINARIO IS NOT NULL))
);

CREATE INDEX IF NOT EXISTS IDX_RESP_PART  ON MEDIMETRIX.RESPOSTA(ID_PARTICIPACAO);
CREATE INDEX IF NOT EXISTS IDX_RESP_AVAL  ON MEDIMETRIX.RESPOSTA(ID_AVALIACAO);
CREATE INDEX IF NOT EXISTS IDX_RESP_QUEST ON MEDIMETRIX.RESPOSTA(ID_QUESTAO);
