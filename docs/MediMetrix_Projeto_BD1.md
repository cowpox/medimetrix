
---
title: "Projeto de Banco de Dados I – Sistema MediMetrix (MMX)"
author: "Adriano Lúcio Uchôa Brandão"
professor: "Prof. Anderson Paulo Ávila Santos"
date: "12/10/2025"
---

# Projeto de Banco de Dados I – Sistema MediMetrix (MMX)
**Tema:** Avaliação de Desempenho Clínico  
**Autor:** Adriano Lúcio Uchôa Brandão  
**Disciplina:** Banco de Dados I  
**Professor:** Prof. Anderson Paulo Ávila Santos  
**Data:** October de 2025

---

## 1. Proposta Geral
O presente projeto tem como objetivo desenvolver o sistema **MediMetrix (MMX)** — uma aplicação web voltada à **avaliação de desempenho clínico de profissionais de saúde**, com foco em **sigilo, integridade e análise quantitativa de resultados**.  

O MediMetrix permitirá o **cadastro, aplicação e análise de avaliações estruturadas**, possibilitando o acompanhamento da evolução dos profissionais e a geração de **relatórios analíticos e gráficos** com base em **consultas SQL explícitas**.  

Além de cumprir os requisitos técnicos da disciplina, o sistema busca promover **melhoria contínua**, **feedback ético** e **decisões baseadas em dados**.

---

## 2. Contextualização
A avaliação de desempenho clínico é essencial para garantir **qualidade assistencial, empatia e segurança** no cuidado em saúde. Entretanto, o processo avaliativo deve ser conduzido de forma **ética e sigilosa**, de modo que as informações coletadas possam ser utilizadas para **aperfeiçoamento profissional** e **gestão de qualidade**, sem comprometer a privacidade dos participantes.  

O MediMetrix foi projetado para atender a essa demanda, integrando **boas práticas de engenharia de software**, **modelagem de banco de dados relacional** e **mecanismos de confidencialidade** inspirados em abordagens de anonimização de dados.

---

## 3. Objetivos Específicos
- Desenvolver um sistema que permita **cadastrar, aplicar e analisar avaliações clínicas** compostas por diferentes tipos de questões (múltipla escolha, texto livre e sim/não).  
- Implementar **níveis configuráveis de sigilo** para as avaliações, de acordo com o grau de confidencialidade exigido.  
- Garantir **integridade e unicidade** dos dados armazenados, assegurando que cada participante responda apenas uma vez.  
- Oferecer **relatórios estatísticos e comparativos** com cálculos de médias, rankings e evolução temporal.  
- Proporcionar **painéis visuais** com gráficos e tabelas baseados em consultas SQL que explorem junções, agregações, subconsultas e filtros avançados.  
- Adotar **boas práticas de desenvolvimento em camadas**, utilizando tecnologias abertas e robustas.

---

## 4. Escopo Funcional do Sistema
O sistema MediMetrix é composto por cinco módulos principais, que representam o ciclo completo de funcionamento da aplicação: **cadastro**, **aplicação**, **análise**, **segurança** e **gestão**.

---

### 4.1 Módulo de Cadastro e Configuração
Responsável pela definição dos elementos estruturais das avaliações.

**Funcionalidades principais:**
- Cadastro de **usuários**, classificados por papel (`ADMIN`, `MEDICO`, `AVALIADOR`, `GESTOR`), com vínculo à especialidade e unidade de trabalho.  
- Cadastro de **especialidades** e **unidades** (usadas em relatórios e agrupamentos).  
- Cadastro de **critérios de avaliação** (ex.: Comunicação, Empatia, Segurança).  
- Cadastro de **questões**, associadas a critérios e com tipos distintos (`LIKERT`, `TEXTO`, `BOOLEANO`).  
- Cadastro de **avaliações**, contendo título, descrição, período de aplicação, política de sigilo e status (`RASCUNHO`, `PUBLICADA`, `ENCERRADA`).  
- Associação de **questões às avaliações** e definição de **ordem de exibição**.  
- Definição de **pesos por critério** (para cálculo ponderado de médias).  
- Cadastro opcional de **metas** de desempenho por critério, especialidade ou unidade (para relatórios comparativos).  

Essas funcionalidades garantem a consistência estrutural dos dados e facilitam a manutenção do banco.

---

### 4.2 Módulo de Aplicação das Avaliações
Controla a execução do processo avaliativo, desde a criação das participações até o registro das respostas.

**Funcionalidades principais:**
- Criação automática de **participações** (quem avalia quem), com controle de unicidade.  
- **Validação de prazos** (`dt_inicio` e `dt_fim`), bloqueando o envio de respostas após o encerramento.  
- Geração de **tokens únicos (UUID)** para garantir a individualidade e o anonimato nas avaliações anônimas.  
- Registro das **respostas** às questões, com coerência entre tipo de questão e valor armazenado.  
- Atualização do **status da participação** (pendente, respondida, expirada).  
- Controle de **integridade referencial** entre avaliações, questões e respostas.  

Esse módulo garante que o fluxo de coleta de dados ocorra de forma segura, controlada e auditável.

---

### 4.3 Módulo de Relatórios e Consultas
Permite o acesso e a interpretação dos resultados obtidos a partir das avaliações aplicadas.

**Funcionalidades principais:**
- **Relatório de médias por critério**, individual e comparativo.  
- **Relatório de evolução temporal**, permitindo análise de desempenho ao longo de diferentes períodos.  
- **Relatório de ranking** por unidade, especialidade ou profissional.  
- **Relatório de variância das respostas**, identificando itens com maior dispersão.  
- **Relatório de comparação com metas**, mostrando a diferença (`delta`) entre a média e o alvo definido.  
- **Painel gerencial consolidado**, com filtros por período, unidade, especialidade e política de sigilo.  
- **Relatório de cobertura**, indicando a porcentagem de respostas concluídas em relação ao total de participações esperadas.  
- **Exportação de relatórios** em formato CSV ou PDF, para análises externas.  

Essas consultas serão implementadas com **SQL explícito**, utilizando recursos como `JOIN`, `GROUP BY`, `AVG()`, `STDDEV()`, `CASE`, `ORDER BY`, `RANK()` e subconsultas.

---

### 4.4 Módulo de Segurança e Sigilo
Responsável pela confidencialidade das informações, assegurando que o tratamento de dados esteja de acordo com a política de sigilo definida em cada avaliação.

**Níveis de sigilo:**

| Política de Sigilo | Descrição |
|--------------------|------------|
| **ABERTO** | As respostas são associadas ao avaliador e visíveis nos relatórios. |
| **CONFIDENCIAL** | As respostas são identificadas no banco, mas ocultadas em relatórios e visões públicas (pseudonimização). |
| **ANONIMO** | As respostas não mantêm vínculo com o avaliador; relatórios são exibidos apenas se o número de respostas for maior ou igual ao limite definido (`k_minimo`). |

**Funcionalidades adicionais:**
- Campo `politica_sigilo` na tabela `avaliacao`, com valores `{ABERTO, CONFIDENCIAL, ANONIMO}`.  
- Controle de **k-anonymity**, com bloqueio automático de relatórios se `n < k_minimo`.  
- **Tokenização de participações** para garantir resposta única por avaliador.  
- **View SQL (vw_respostas_confidenciais)** que oculta campos sensíveis em relatórios.  
- **Registro de data/hora da resposta** (`respondeu_em`).  
- **Logs de auditoria administrativa**, registrando alterações em avaliações, usuários e políticas de sigilo.  

Esse módulo diferencia o MediMetrix de sistemas avaliativos comuns, assegurando ética e privacidade nos dados.

---

### 4.5 Módulo de Gestão de Usuários e Acesso
Garante a organização dos perfis e das permissões dentro do sistema.

**Funcionalidades principais:**
- Controle de **papéis de usuário** (`ADMIN`, `MEDICO`, `AVALIADOR`, `GESTOR`).  
- **Autenticação** por credenciais básicas (login e senha).  
- Permissões diferenciadas:  
  - `ADMIN`: gerencia cadastros e políticas de sigilo;  
  - `MEDICO`: realiza autoavaliações e visualiza relatórios pessoais;  
  - `AVALIADOR`: responde avaliações 360°;  
  - `GESTOR`: visualiza relatórios agregados, sem acesso individual a respostas.  
- **Desativação de usuários** sem perda de histórico (`ativo = false`).  
- Filtros de relatórios por **especialidade, unidade e período**.

---

## 5. Tecnologias e Arquitetura
- **Back-end:** Java/J2EE com **Spring Boot e JDBC Template** (sem uso de JPA/Hibernate).  
- **Front-end:** Thymeleaf e **Chart.js** (renderização de gráficos e tabelas no servidor).  
- **Banco de Dados:** PostgreSQL, utilizando **chaves primárias e estrangeiras**, **restrições de integridade**, **validações CHECK** e **índices** para otimização das consultas.  
- **Camadas da aplicação:**

| Camada | Função |
|---------|--------|
| **Model** | Entidades e mapeamento de dados |
| **DAO/Repository** | Consultas SQL explícitas |
| **Service/Controller** | Regras de negócio e controle de fluxo |
| **View** | Templates HTML e gráficos interativos |

---

## 6. Resultados Esperados
O MediMetrix permitirá:
- Identificar **médias e tendências de desempenho** individuais e coletivas.  
- Visualizar **evoluções temporais** e **comparações entre unidades e especialidades**.  
- Avaliar **aderência às metas** e detectar **pontos críticos de dispersão**.  
- Garantir **anonimato e confidencialidade** conforme a política definida.  
- Gerar **relatórios exportáveis e visualizações gráficas** de alta clareza.  

Esses resultados demonstrarão o domínio da **linguagem SQL** e a aplicação prática de **modelagem relacional com integridade referencial**.

---

## 7. Entrega 1 – Escopo
Esta primeira entrega compreenderá:
1. **Descrição detalhada do sistema e de seus fluxos funcionais**;  
2. **Modelagem conceitual (Diagrama ER)**, sem redundâncias e com cardinalidades definidas;  
3. **Esquema relacional** normalizado e mapeado para PostgreSQL;  
4. **Script SQL** de criação do banco, com restrições de integridade e chaves;  
5. **Protótipos visuais (mockups)** dos relatórios esperados (gráficos e tabelas);  
6. **Explicação técnica das consultas SQL** planejadas para geração dos relatórios.

---

## 8. Conclusão
O sistema **MediMetrix (MMX)** propõe uma abordagem moderna e ética para a avaliação de desempenho clínico, aliando **rigor técnico de modelagem de dados**, **segurança da informação** e **análise quantitativa baseada em SQL**.  
Sua arquitetura modular e política de sigilo configurável demonstram o equilíbrio entre **funcionalidade prática** e **boas práticas de desenvolvimento**, resultando em um projeto de alto valor técnico e acadêmico.

---
