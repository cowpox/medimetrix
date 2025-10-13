-- Este script deve ser executado já conectado em medimetrix_db
-- psql: \c medimetrix_db

-- Schema do projeto
CREATE SCHEMA IF NOT EXISTS MEDIMETRIX AUTHORIZATION CURRENT_USER;

-- Usar o schema MEDIMETRIX por padrão nesta sessão
SET SEARCH_PATH TO MEDIMETRIX, public;

-- (opcional) Verificação rápida do schema ativo
-- SHOW SEARCH_PATH;
