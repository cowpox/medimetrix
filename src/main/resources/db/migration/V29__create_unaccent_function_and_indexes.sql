-- ============================================================
-- V29__create_unaccent_function_and_indexes.sql
-- Cria função imutável e índices para busca sem acento/caixa
-- Compatível com instalações onde NÃO existe a sobrecarga
-- unaccent(regdictionary, text)
-- ============================================================

SET SEARCH_PATH TO medimetrix, public;

-- 0) (opcional, idempotente) garantir extensão no schema público
--    Se você já criou no V28, isso só confirma e não falha.
CREATE EXTENSION IF NOT EXISTS unaccent SCHEMA public;

-- 1) Função wrapper imutável usando a versão 1-arg do unaccent
CREATE OR REPLACE FUNCTION immutable_unaccent(txt text)
RETURNS text
AS $$
SELECT public.unaccent($1);
$$ LANGUAGE SQL
IMMUTABLE
PARALLEL SAFE;

COMMENT ON FUNCTION immutable_unaccent(text) IS
'Wrapper imutável de public.unaccent(text) para uso em índices.';

-- 2) Índices funcionais
CREATE INDEX IF NOT EXISTS IDX_UNIDADE_NOME_UNACCENT
    ON medimetrix.unidade (immutable_unaccent(lower(nome)));

CREATE INDEX IF NOT EXISTS IDX_UNIDADE_GESTOR
    ON medimetrix.unidade (gestor_usuario_id);

CREATE INDEX IF NOT EXISTS IDX_UNIDADE_ATIVO
    ON medimetrix.unidade (ativo);

COMMENT ON INDEX medimetrix.IDX_UNIDADE_NOME_UNACCENT IS
'Busca insensível a acentos e caixa em UNIDADE.NOME';

COMMENT ON INDEX medimetrix.IDX_UNIDADE_GESTOR IS
'Filtro por gestor (FK → USUARIO)';

COMMENT ON INDEX medimetrix.IDX_UNIDADE_ATIVO IS
'Filtro rápido por status ativo';
