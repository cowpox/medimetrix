-- =========================================
-- Script: 14_create_admin.sql
-- Projeto: MediMetrix
-- Schema : MEDIMETRIX
-- =========================================
-- Pré-requisitos:
--   \c medimetrix_db
--   SET SEARCH_PATH TO MEDIMETRIX, public;
--   Tabela MEDIMETRIX.USUARIO já criada.

CREATE TABLE MEDIMETRIX.ADMIN (
                                  USUARIO_ID   BIGINT,

                                  CONSTRAINT "PK_ADMIN" PRIMARY KEY (USUARIO_ID),

                                  CONSTRAINT "FK_ADMIN_USUARIO"
                                      FOREIGN KEY (USUARIO_ID)
                                          REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                          ON UPDATE RESTRICT
                                          ON DELETE CASCADE
);

-- (comentário) Triggers quando forem vistos em aula:
--  * BEFORE INSERT/UPDATE em ADMIN: garantir USUARIO.PAPEL = 'ADMIN'
--  * AFTER UPDATE em USUARIO: bloquear mudança de PAPEL se existir linha em ADMIN
