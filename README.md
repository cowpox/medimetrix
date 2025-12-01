# 🩺 MediMetrix (MMX)
## ⭐ Entrega Final — Relatórios e Insights
Sistema de Avaliação de Desempenho Clínico

O **MediMetrix (MMX)** é um sistema completo para avaliações clínicas de profissionais da saúde, combinando modelagem sólida, arquitetura hexagonal e um módulo avançado de **Relatórios e Insights**, foco desta entrega final.

O objetivo é permitir que gestores visualizem indicadores consolidados da avaliação, distribuídos por escopos, critérios, unidades e especialidades — incluindo análise heurística e insights automáticos auxiliados por IA (Gemini).

---

# 📌 Sumário

1. Visão Geral  
2. Objetivo da Entrega Final  
3. Arquitetura do Sistema  
4. Regras Essenciais de Negócio  
5. Relatórios Implementados  
6. Insights Automáticos  
7. Estrutura de Pastas  
8. Considerações Finais  

---

# 🧭 Visão Geral

O MediMetrix oferece:

- Avaliações estruturadas por **critérios e questões**  
- Geração automática de **participações**  
- Relatórios consolidados  
- Insights heurísticos + Gemini  

---

# 🎯 Objetivo da Entrega Final

A entrega final concentra-se no módulo de **Relatórios e Insights**, incluindo:

- Consolidação de indicadores  
- Distribuições por critérios  
- Comparações por unidades e especialidades  
- Insights gerados automaticamente  

---

# 🏛 Arquitetura do Sistema

Arquitetura **Hexagonal**, com:

- **Domain** — entidades e lógica  
- **Application** — casos de uso e serviços  
- **Infrastructure** — repositórios, SQL, migrações  
- **Web** — controllers e templates  

---

# 📐 Regras Essenciais de Negócio

Escopos:

- **GLOBAL**  
- **UNIDADE**  
- **ESPECIALIDADE**

Participações são geradas automaticamente ao publicar a avaliação.

---

# 📊 Relatórios Implementados

- Visão geral da avaliação  
- Distribuição por critérios  
- Consolidação por unidade / especialidade  
- Destaques e pontos de atenção  

---

# 🧠 Insights Automáticos

- Heurística interna  
- Integração com **Gemini** (opcional mediante API key)  

---

# 📂 Estrutura de Pastas

```
src/
 ├── domain
 ├── application
 ├── infrastructure
 └── web
docs/
```

---

# 🧾 Considerações Finais

A entrega final consolida um módulo completo de relatórios e insights, alinhado ao objetivo do sistema e pronto para expansão.
