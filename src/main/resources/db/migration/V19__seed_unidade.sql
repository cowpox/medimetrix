-- Auto-generated seed for 'Unidade' → UNIDADE
-- Version V19
SET SEARCH_PATH TO MEDIMETRIX, public;

INSERT INTO MEDIMETRIX.UNIDADE (ID_UNIDADE, NOME, GESTOR_USUARIO_ID, ATIVO) VALUES
  (1, 'PRONTO-SOCORRO', 3, TRUE),
  (2, 'AMBULATÓRIO', 15, TRUE),
  (3, 'ENFERMARIA', 25, TRUE),
  (4, 'PRONTO-ATENDIMENTO', 39, TRUE),
  (5, 'SALA DE EMERGÊNCIA', 87, TRUE),
  (6, 'MATERNIDADE', 49, TRUE),
  (7, 'PEDIATRIA', 58, TRUE),
  (8, 'UTI', 69, TRUE),
  (9, 'CENTRO CIRÚRGICO', 78, TRUE);

-- Sync sequence for UNIDADE.ID_UNIDADE
SELECT setval(
               pg_get_serial_sequence('medimetrix.unidade', 'id_unidade'),
               COALESCE((SELECT MAX(id_unidade) FROM medimetrix.unidade), 0),
               true
       );
