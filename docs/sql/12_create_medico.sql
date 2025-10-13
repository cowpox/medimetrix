-- =========================================
-- Script: 12_create_medico.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisitos:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   USUARIO, UNIDADE e ESPECIALIDADE já criadas.

CREATE TABLE MEDIMETRIX.MEDICO (
                                   USUARIO_ID        BIGINT,            -- PK e FK 1:1 com USUARIO
                                   ESPECIALIDADE_ID  BIGINT  NOT NULL,
                                   UNIDADE_ID        BIGINT  NOT NULL,

                                   CRM_NUM           VARCHAR(30) NOT NULL,
                                   CRM_UF            CHAR(2)     NOT NULL,

                                   CONSTRAINT "PK_MEDICO" PRIMARY KEY (USUARIO_ID),

                                   CONSTRAINT "FK_MEDICO_USUARIO"
                                       FOREIGN KEY (USUARIO_ID)
                                           REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                           ON UPDATE RESTRICT ON DELETE CASCADE,   -- conforme manifesto (1:1)

                                   CONSTRAINT "FK_MEDICO_ESPECIALIDADE"
                                       FOREIGN KEY (ESPECIALIDADE_ID)
                                           REFERENCES MEDIMETRIX.ESPECIALIDADE(ID_ESPECIALIDADE)
                                           ON UPDATE RESTRICT ON DELETE RESTRICT,

                                   CONSTRAINT "FK_MEDICO_UNIDADE"
                                       FOREIGN KEY (UNIDADE_ID)
                                           REFERENCES MEDIMETRIX.UNIDADE(ID_UNIDADE)
                                           ON UPDATE RESTRICT ON DELETE RESTRICT,

    -- (Opcional recomendado) impede duplicidade institucional de CRM
                                   CONSTRAINT "UQ_MEDICO_CRM_UF" UNIQUE (CRM_NUM, CRM_UF),

    -- Checagem simples das UFs (pode virar DOMAIN depois)
                                   CONSTRAINT "CK_MEDICO_UF_BR"
                                       CHECK (CRM_UF IN ('AC','AL','AM','AP','BA','CE','DF','ES','GO','MA','MG','MS','MT',
                                                         'PA','PB','PE','PI','PR','RJ','RN','RO','RR','RS','SC','SE','SP','TO'))
);

-- Índices úteis
CREATE INDEX IF NOT EXISTS "IDX_MEDICO_ESPECIALIDADE" ON MEDIMETRIX.MEDICO(ESPECIALIDADE_ID);
CREATE INDEX IF NOT EXISTS "IDX_MEDICO_UNIDADE"       ON MEDIMETRIX.MEDICO(UNIDADE_ID);

-- Ativar pendências
ALTER TABLE MEDIMETRIX.PARTICIPACAO
    ADD CONSTRAINT "FK_PART_MEDICO"
        FOREIGN KEY (AVALIADO_MEDICO_ID)
            REFERENCES MEDIMETRIX.MEDICO(USUARIO_ID)
            ON UPDATE RESTRICT ON DELETE RESTRICT;
