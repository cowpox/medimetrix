-- Auto-generated seed for 'Criterio' → CRITERIO
-- Version V21
SET SEARCH_PATH TO MEDIMETRIX, public;

INSERT INTO MEDIMETRIX.CRITERIO (ID_CRITERIO, NOME, DEFINICAO_OPERACIONAL, DESCRICAO, ATIVO, PESO, ORDEM_SUGERIDA, VERSAO, DATA_CRIACAO, DATA_ULTIMA_EDICAO) VALUES
  (1, 'Segurança do Paciente', 'Cumprimento das práticas e protocolos que reduzem riscos ao paciente.', 'Avalia a adesão às normas de segurança (checklists, identificação, prevenção de eventos adversos).', TRUE, 1, 1, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (2, 'Técnica e Conhecimento Científico', 'Aplicação adequada de conhecimentos técnicos e científicos no atendimento.', 'Mede a competência técnica e atualização em condutas baseadas em evidências.', TRUE, 1, 2, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (3, 'Comunicação e Relacionamento', 'Clareza e empatia na comunicação com pacientes, equipe e colegas.', 'Analisa a escuta ativa, respeito e colaboração nas interações interpessoais.', TRUE, 1, 3, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (4, 'Organização e Produtividade', 'Capacidade de planejar, cumprir horários e manter fluxo eficiente.', 'Observa pontualidade, gestão do tempo e registro adequado das atividades.', TRUE, 1, 4, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (5, 'Adesão a Protocolos e Diretrizes', 'Uso consistente de diretrizes clínicas e condutas institucionais.', 'Mede a padronização e conformidade com protocolos internos e externos.', TRUE, 1, 5, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (6, 'Responsabilidade e Ética Profissional', 'Cumprimento das normas éticas e de confidencialidade.', 'Avalia conduta ética, compromisso com o paciente e a instituição.', TRUE, 1, 6, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (7, 'Trabalho em Equipe', 'Participação e cooperação efetiva com outros profissionais de saúde.', 'Analisa colaboração interdisciplinar e suporte mútuo nas rotinas.', TRUE, 1, 7, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (8, 'Humanização e Empatia', 'Capacidade de compreender e acolher o paciente de forma integral.', 'Observa sensibilidade, respeito e empatia diante das necessidades humanas.', TRUE, 1, 8, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (9, 'Gestão de Informações Clínicas', 'Registro e utilização correta dos dados clínicos e sistemas eletrônicos.', 'Avalia a completude, precisão e uso ético das informações em prontuário e sistemas.', TRUE, 1, 9, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00'),
  (10, 'Educação Continuada e Inovação', 'Participação em atividades de aprendizado e uso de novas práticas.', 'Mede engajamento com atualização científica e incorporação de inovações assistenciais.', TRUE, 1, 10, 1, TIMESTAMP '2025-10-24 09:00:00', TIMESTAMP '2025-10-24 09:00:00');

-- Sync sequence for CRITERIO.ID_CRITERIO
SELECT setval(
               pg_get_serial_sequence('medimetrix.criterio', 'id_criterio'),
               COALESCE((SELECT MAX(id_criterio) FROM medimetrix.criterio), 0),
               true
       );
