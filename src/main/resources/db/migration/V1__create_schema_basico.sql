-- ============================================================
-- V1__create_schema_basico.sql
-- Criação do schema principal do projeto MediMetrix
-- ============================================================

-- Certifique-se de estar conectado ao banco medimetrix_db antes de executar

CREATE SCHEMA IF NOT EXISTS MEDIMETRIX AUTHORIZATION CURRENT_USER;

-- Define o schema padrão da sessão
SET SEARCH_PATH TO MEDIMETRIX, public;
