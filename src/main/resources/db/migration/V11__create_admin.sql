-- ============================================================
-- V11__create_admin.sql
-- Criação da tabela ADMIN (subtipo de USUARIO)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.ADMIN (
                                  USUARIO_ID BIGINT,

                                  CONSTRAINT PK_ADMIN PRIMARY KEY (USUARIO_ID),

                                  CONSTRAINT FK_ADMIN_USUARIO
                                      FOREIGN KEY (USUARIO_ID)
                                          REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                          ON UPDATE RESTRICT
                                          ON DELETE CASCADE
);
