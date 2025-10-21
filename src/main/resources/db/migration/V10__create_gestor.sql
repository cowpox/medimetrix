-- ============================================================
-- V10__create_gestor.sql
-- Criação da tabela GESTOR (subtipo de USUARIO)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.GESTOR (
                                   USUARIO_ID BIGINT,

                                   CONSTRAINT PK_GESTOR PRIMARY KEY (USUARIO_ID),

                                   CONSTRAINT FK_GESTOR_USUARIO
                                       FOREIGN KEY (USUARIO_ID)
                                           REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
                                           ON UPDATE RESTRICT
                                           ON DELETE CASCADE
);
