-- ============================================================
-- V16__add_fk_participacao_medico.sql
-- Adiciona FK PARTICIPACAO → MEDICO (subtipo de USUARIO)
-- ============================================================

SET SEARCH_PATH TO MEDIMETRIX, public;

ALTER TABLE MEDIMETRIX.PARTICIPACAO
    ADD CONSTRAINT FK_PART_MEDICO
        FOREIGN KEY (AVALIADO_MEDICO_ID)
            REFERENCES MEDIMETRIX.MEDICO(USUARIO_ID)
            ON UPDATE RESTRICT
            ON DELETE RESTRICT;
