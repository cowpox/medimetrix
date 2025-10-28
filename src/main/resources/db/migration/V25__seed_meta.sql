-- Auto-generated seed for 'Meta' → META
-- Version V25
SET SEARCH_PATH TO MEDIMETRIX, public;

INSERT INTO MEDIMETRIX.META (ID_META, ID_CRITERIO, ID_UNIDADE, ID_ESPECIALIDADE, ALVO, OPERADOR, VIGENCIA_INICIO, VIGENCIA_FIM, ATIVO, PRIORIDADE, JUSTIFICATIVA, DATA_CRIACAO, DATA_ULTIMA_EDICAO) VALUES
  (1, 1, NULL, NULL, 4.4, '>=', DATE '2025-01-01', NULL, TRUE, 1, 'Padrão de excelência em Segurança do Paciente.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (2, 2, NULL, NULL, 4.3, '>=', DATE '2025-01-01', NULL, TRUE, 1, 'Domínio técnico e atualização baseada em evidências.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (3, 3, NULL, NULL, 4.2, '>=', DATE '2025-01-01', NULL, TRUE, 2, 'Melhorar comunicação clínica e trabalho colaborativo.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (4, 4, NULL, NULL, 4.1, '>=', DATE '2025-01-01', NULL, TRUE, 2, 'Aprimorar organização do fluxo e pontualidade.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (5, 5, NULL, NULL, 4.2, '>=', DATE '2025-01-01', NULL, TRUE, 1, 'Conformidade com protocolos e diretrizes institucionais.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (6, 6, NULL, NULL, 4.5, '>=', DATE '2025-01-01', NULL, TRUE, 1, 'Conduta ética, responsabilidade e confidencialidade.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (7, 7, NULL, NULL, 4.2, '>=', DATE '2025-01-01', NULL, TRUE, 2, 'Cooperação efetiva e suporte à equipe multiprofissional.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (8, 8, NULL, NULL, 4.3, '>=', DATE '2025-01-01', NULL, TRUE, 1, 'Atendimento humanizado, empatia e acolhimento.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (9, 9, NULL, NULL, 4.1, '>=', DATE '2025-01-01', NULL, TRUE, 2, 'Gestão correta de registros e uso responsável dos sistemas.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (10, 10, NULL, NULL, 4, '>=', DATE '2025-01-01', NULL, TRUE, 3, 'Engajamento em educação continuada e inovação.', TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00');

-- Sync sequence for META.ID_META
SELECT setval(
               pg_get_serial_sequence('medimetrix.meta', 'id_meta'),
               COALESCE((SELECT MAX(id_meta) FROM medimetrix.meta), 0),
               true
       );
