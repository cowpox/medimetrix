-- ============================================================
-- V8__create_usuario.sql
-- Criação da tabela USUARIO e FK em UNIDADE
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

CREATE TABLE MEDIMETRIX.USUARIO (
                                    ID_USUARIO         BIGSERIAL,
                                    NOME               VARCHAR(120) NOT NULL,
                                    EMAIL              VARCHAR(255) NOT NULL,
                                    ATIVO              BOOLEAN NOT NULL DEFAULT TRUE,
                                    PAPEL              VARCHAR(10) NOT NULL,  -- MEDICO | GESTOR | ADMIN

                                    DATA_CRIACAO       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                    DATA_ULTIMA_EDICAO TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                    CONSTRAINT PK_USUARIO PRIMARY KEY (ID_USUARIO),
                                    CONSTRAINT CK_USUARIO_PAPEL CHECK (PAPEL IN ('MEDICO', 'GESTOR', 'ADMIN')),
                                    CONSTRAINT CK_USUARIO_NOME_NAO_VAZIO CHECK (LENGTH(TRIM(NOME)) > 0),
                                    CONSTRAINT CK_USUARIO_EMAIL_NAO_VAZIO CHECK (LENGTH(TRIM(EMAIL)) > 0)
);

-- Unicidade de e-mail (case-insensitive)
CREATE UNIQUE INDEX IF NOT EXISTS UQ_USUARIO_EMAIL_CI
    ON MEDIMETRIX.USUARIO (LOWER(EMAIL));

CREATE INDEX IF NOT EXISTS IDX_USUARIO_PAPEL ON MEDIMETRIX.USUARIO(PAPEL);
CREATE INDEX IF NOT EXISTS IDX_USUARIO_ATIVO ON MEDIMETRIX.USUARIO(ATIVO);

-- FK UNIDADE → USUARIO (gestor responsável)
ALTER TABLE MEDIMETRIX.UNIDADE
    ADD CONSTRAINT FK_UNIDADE_GESTOR
        FOREIGN KEY (GESTOR_USUARIO_ID)
            REFERENCES MEDIMETRIX.USUARIO(ID_USUARIO)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;
