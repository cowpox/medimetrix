# 🩺 MediMetrix (MMX)
# 🎓 Entrega Final — Documentação Completa  
Sistema de Avaliação de Desempenho Clínico  
Curso: Ciência de Dados e Inteligência Artificial — UEL  

---

# 📘 Sumário

1. Introdução  
2. Objetivos da Entrega Final  
3. Arquitetura do Projeto  
4. Modelagem e Regras de Negócio  
5. Relatórios Implementados  
6. Insights Heurísticos e Gemini  
7. Fluxo de Geração de Participações  
8. Arquivos, Pastas e Organização  
9. Conclusão  

---

# 🧭 Introdução

O MediMetrix é um sistema destinado a avaliações formais de desempenho clínico, oferecendo indicadores consolidados e insights automáticos. Esta documentação detalha o módulo entregue para a **Entrega Final**, com foco em **relatórios e insights**.

---

# 🎯 Objetivos da Entrega Final

- Construção completa dos relatórios analíticos  
- Implementação do módulo de insights heurísticos e Gemini  
- Coerência entre backend, lógica SQL e frontend  
- Consolidação dos dados de desempenho  

---

# 🏛 Arquitetura do Projeto

Arquitetura **Hexagonal**, composta por:

## 🔹 Domain  
Entidades e regras essenciais: Avaliação, Participação, Critério, Questão etc.

## 🔹 Application  
Casos de uso e serviços principais, incluindo:  
- RelatorioAvaliacaoService  
- RelatorioAvaliacaoInsightsService  

## 🔹 Infrastructure  
- Repositórios SQL  
- Migrações Flyway  
- Integração Gemini  

## 🔹 Web  
- Controllers  
- Templates Thymeleaf  
- Lógica de navegação dos relatórios  

---

# 📐 Modelagem e Regras de Negócio

## 📌 Escopos
- **GLOBAL** → todos os médicos ativos  
- **UNIDADE** → médicos de uma unidade  
- **ESPECIALIDADE** → médicos de uma especialidade  

## 📌 Participações
Geradas automaticamente ao publicar a avaliação, com snapshots de unidade e especialidade.

## 📌 Respostas
Notas vinculadas a participações e questões, servindo como base para as agregações dos relatórios.

---

# 📊 Relatórios Implementados

## 1) Visão Geral  
Inclui médias globais, limites, faixas de desempenho e taxas de resposta.

## 2) Distribuição por Critérios  
Cálculos para cada critério:
- Média  
- Mínimo / Máximo  
- Contagem  
- Badge de desempenho (5 níveis)  

## 3) Consolidação por Unidade / Especialidade  
Dependendo do escopo publicado, o relatório reflete:
- Comportamento global  
- Comparativo por unidade  
- Comparativo por especialidade  

---

# 🧠 Insights Heurísticos e Gemini

## ✔ Heurística interna
Analisa padrões de desempenho e gera texto interpretativo automático.

## ✔ Integração com Gemini
Mediante variável `GEMINI_API_KEY`, o sistema envia contexto e recebe insights avançados da IA.

---

# 🔃 Fluxo de Geração de Participações

1. Avaliação criada em rascunho  
2. Gestor define escopo  
3. Ao publicar:
   - Confirmação estilizada é exibida  
   - Participações são geradas conforme escopo  
4. Respostas alimentam os relatórios  

---

# 📂 Arquivos, Pastas e Organização

```
src/main/java/com/mmx/medimetrix/
 ├── domain
 ├── application
 ├── infrastructure
 └── web

src/main/resources/
 ├── templates/gestor/relatorios
 └── db/migration

docs/
 └── README-completo.md
```

---

# 🧾 Conclusão

A entrega final integra todas as camadas do MediMetrix em um módulo completo de relatórios e insights, apresentando:

- Indicadores precisos  
- Organização arquitetural sólida  
- Interface clara  
- Suporte a interpretações avançadas via IA  

O sistema está pronto para evolução futura e integração com dashboards mais avançados.
