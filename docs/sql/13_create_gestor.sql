-- =========================================
-- Script: 13_create_gestor.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisitos:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabela MEDIMETRIX.USUARIO já criada.

CREATE TABLE MEDIMETRIX.GESTOR (
                                   USUARIO_ID   BIGINT,

                                   CONSTRAINT "PK_GESTOR" PRIMARY KEY (USUARIO_ID),

                                   CONSTRAINT "FK_GESTOR_USUARIO"
                                       FOREIGN KEY (USUARIO_ID)
                                           REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                           ON UPDATE RESTRICT
                                           ON DELETE CASCADE
);

-- (comentário) Quando chegarmos em triggers:
--  * BEFORE INSERT/UPDATE em GESTOR: assert USUARIO.PAPEL='GESTOR'
--  * AFTER UPDATE em USUARIO: impedir mudar PAPEL se existir linha em GESTOR

-- Ativar pendências
-- =========================================
-- Script: 13b_alter_unidade_fk_gestor.sql
-- Pré-requisitos: MEDIMETRIX.UNIDADE e MEDIMETRIX.GESTOR existentes
-- =========================================

-- Se já existe a FK para USUARIO, removemos
DO $$
BEGIN
  IF EXISTS (
    SELECT 1 FROM information_schema.table_constraints
    WHERE constraint_schema = 'MEDIMETRIX'
      AND table_name = 'UNIDADE'
      AND constraint_name = 'FK_UNIDADE_GESTOR'
  ) THEN
ALTER TABLE MEDIMETRIX.UNIDADE
DROP CONSTRAINT "FK_UNIDADE_GESTOR";
END IF;
END$$;

-- Agora criamos a FK para o subtipo GESTOR
ALTER TABLE MEDIMETRIX.UNIDADE
    ADD CONSTRAINT "FK_UNIDADE_GESTOR"
        FOREIGN KEY (GESTOR_USUARIO_ID)
            REFERENCES MEDIMETRIX.GESTOR(USUARIO_ID)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;

