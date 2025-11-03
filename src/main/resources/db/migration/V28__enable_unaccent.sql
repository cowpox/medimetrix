-- ============================================================
-- V28__enable_unaccent.sql
-- Habilita extensão e registra o dicionário unaccent
-- ============================================================

SET SEARCH_PATH TO public;

-- 1. Habilita extensão UNACCENT
CREATE EXTENSION IF NOT EXISTS unaccent;

-- 2. Cria dicionário de texto completo 'unaccent' se ainda não existir
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_catalog.pg_ts_dict WHERE dictname = 'unaccent'
    ) THEN
        CREATE TEXT SEARCH DICTIONARY public.unaccent (
            TEMPLATE = pg_catalog.simple,
            RULES = 'unaccent'
        );
END IF;
END $$;

-- ============================================================
-- Este script termina aqui. O índice funcional será criado na V29.
-- ============================================================
