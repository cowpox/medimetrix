-- ============================================================
-- V9__create_medico.sql
-- Criação da tabela MEDICO (subtipo de USUARIO)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.MEDICO (
                                   USUARIO_ID        BIGINT,          -- PK e FK 1:1 com USUARIO
                                   ESPECIALIDADE_ID  BIGINT NOT NULL,
                                   UNIDADE_ID        BIGINT NOT NULL,

                                   CRM_NUMERO        VARCHAR(20) NOT NULL,
                                   CRM_UF            CHAR(2) NOT NULL,

                                   DATA_CRIACAO      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                   DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                   CONSTRAINT PK_MEDICO PRIMARY KEY (USUARIO_ID),

                                   CONSTRAINT FK_MEDICO_USUARIO
                                       FOREIGN KEY (USUARIO_ID)
                                           REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                           ON UPDATE RESTRICT
                                           ON DELETE CASCADE,

                                   CONSTRAINT FK_MEDICO_ESP
                                       FOREIGN KEY (ESPECIALIDADE_ID)
                                           REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
                                           ON UPDATE RESTRICT
                                           ON DELETE RESTRICT,

                                   CONSTRAINT FK_MEDICO_UNID
                                       FOREIGN KEY (UNIDADE_ID)
                                           REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
                                           ON UPDATE RESTRICT
                                           ON DELETE RESTRICT,

                                   CONSTRAINT UQ_MEDICO_CRM UNIQUE (CRM_NUMERO, CRM_UF),

                                   CONSTRAINT CK_MEDICO_CRM_NUM CHECK (LENGTH(TRIM(CRM_NUMERO)) > 0),

                                   CONSTRAINT CK_MEDICO_UF_BR CHECK (
                                       CRM_UF IN (
                                                  'AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT',
                                                  'PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'
                                           )
                                       )
);

CREATE INDEX IF NOT EXISTS IDX_MEDICO_ESPECIALIDADE ON MEDIMETRIX.MEDICO(ESPECIALIDADE_ID);
CREATE INDEX IF NOT EXISTS IDX_MEDICO_UNIDADE       ON MEDIMETRIX.MEDICO(UNIDADE_ID);
