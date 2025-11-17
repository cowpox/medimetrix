# 🩺 MediMetrix (MMX)
## Sistema de Avaliação de Desempenho Clínico  
### **Entrega 2 — Versão Estendida (Completa e Detalhada)**

O **MediMetrix (MMX)** é um sistema de avaliação de desempenho clínico desenvolvido para a disciplina **Banco de Dados I** do curso de **Ciência de Dados e Inteligência Artificial — UEL**.

Esta versão **estendida** documenta toda a arquitetura, decisões e funcionalidades implementadas até a **Entrega 2**, incluindo detalhes técnicos das camadas, scripts SQL, controllers, templates, e estrutura do projeto.

---

# 📌 Sumário

1. [Visão Geral do Sistema](#visão-geral-do-sistema)  
2. [Requisitos da Disciplina e Conformidade](#requisitos-da-disciplina-e-conformidade)  
3. [Arquitetura Geral](#arquitetura-geral)  
4. [Banco de Dados — Modelagem e Implementação](#banco-de-dados)  
5. [Backend — Controllers, Services e DAOs](#backend)  
6. [Frontend — Thymeleaf, Templates e Navegação](#frontend)  
7. [Funcionalidades Implementadas na Entrega 2](#entrega2)  
8. [Funcionalidades Pendentes para Entrega 3](#entrega3)  
9. [Estrutura Completa do Projeto](#estrutura)  
10. [Como Executar](#como-executar)  
11. [Autor e Créditos](#autor)

---

# <a name="visão-geral-do-sistema"></a>1. 📘 Visão Geral do Sistema

O MediMetrix é uma plataforma que permite:

- Cadastrar *usuários, médicos, unidades, especialidades*
- Criar *catálogo de critérios, metas e questões*
- Criar *avaliações* com datas, sigilo (k-mínimo) e escopo de participantes
- Adicionar e ordenar *questões* de uma avaliação
- Preparar participações e respostas (backend pronto)
- Gerar posteriormente relatórios e dashboards (Entrega 3)

O foco desta entrega é garantir o **ciclo completo de cadastro e execução de avaliações**, atendendo à Etapa 2 exigida pela disciplina.

---

# <a name="requisitos-da-disciplina-e-conformidade"></a>2. 📑 Requisitos da Disciplina e Conformidade

Segundo o documento oficial da disciplina (**Projeto BD1_CDIA.pdf**) — incluído no repositório, as entregas são:

### ✔ Entrega 1  
- Documentação do sistema  
- Modelagem conceitual e relacional  
- Script SQL do banco  
- Prototipação e definição dos relatórios  

**Status:** *entrega concluída e documentada nos arquivos da pasta `docs/`*.

---

### ✔ Entrega 2 (16/11)  
> “Banco de dados funcional e sistema rodando com funcionalidades de cadastro e realização de avaliações.”

Esta entrega requer:
- Banco operacional com cargas
- CRUDs completos
- Avaliações funcionando de ponta a ponta
- Registro de respostas *ao menos no backend*

**Status:** *entrega totalmente concluída neste commit*.

---

### ⏳ Entrega 3 (01/12)  
> “Sistema finalizado, incluindo relatórios.”

Será feito posteriormente com **React/JSP** conforme exigência da disciplina.

---

# <a name="arquitetura-geral"></a>3. 🏛 Arquitetura Geral do Projeto

Arquitetura em **camadas**, sem uso de JPA/Hibernate (restrição da disciplina):

- **Web MVC** — controllers Spring Boot + Thymeleaf  
- **Application** — services, regras de negócio, commands/queries  
- **Infrastructure** — DAOs com SQL explícito  
- **Domain** — modelos Rich Domain  
- **DB** — PostgreSQL + Flyway

Fluxo padrão do sistema:

```
Controller → Service → DAO → SQL → PostgreSQL
```

---

# <a name="banco-de-dados"></a>4. 🗃 Banco de Dados — Modelagem e Implementação

### ✔ Modelagem completa  
- MER (diagrama ER)  
- Esquema relacional normalizado (3FN)  
- 14 tabelas principais  
- 2000+ linhas de seeds opcionais (catálogos e médicos)

### ✔ Implementação via Flyway  
Scripts numerados:

```
V1__create_criterio.sql
V2__create_questao.sql
V3__create_avaliacao.sql
...
V14__create_admin.sql
...
```

Inclui:
- PK / FK
- Cascades adequados
- CHECK constraints
- Índices para desempenho
- Colunas de auditoria (`data_criacao`, `data_ultima_edicao`)

---

# <a name="backend"></a>5. ⚙ Backend — Controllers, Services e DAOs

### ✔ Controllers implementados
- `Admin → Usuarios, Medicos, Unidades, Especialidades`
- `Gestor → Critérios, Metas, Questões, Avaliações`
- `Médico → Acesso estrutural já pronto`

### ✔ Service Layer  
- `AvaliacaoService`  
- `AvaliacaoQuestaoService`  
- `QuestaoService`  
- `ParticipacaoService`  
- `RespostaService`  

### ✔ DAOs (SQL explícito)  
- Consultas, inserções, atualizações e deletes com SQL manual.
- Uso de `JdbcTemplate`.

---

# <a name="frontend"></a>6. 🎨 Frontend — Thymeleaf e Templates

### ✔ Layout principal
- Template base (`_layouts/default`)
- Sidebar dinâmica conforme papel do usuário (Admin, Gestor, Médico)

### ✔ Telas implementadas
- Listas com filtros e ordenação
- Formulários com validação e alerts
- Modais e interação mínima em JS

---

# <a name="entrega2"></a>7. 🎯 Funcionalidades Implementadas na Entrega 2

### ✔ Cadastro de Avaliações  
Inclui:
- Título  
- Datas de aplicação  
- Status  
- Campo **k-mínimo** para anonimato  
- Campo **escopo** para seleção de participantes:  
  - GLOBAL  
  - UNIDADE  
  - ESPECIALIDADE  
- Select dinâmico de Unidade ou Especialidade

### ✔ Edição de Avaliações  
- Regras: avaliações PUBLICADAS ou ENCERRADAS não podem ser alteradas  
- Mensagens amigáveis via redirect

### ✔ Questões das Avaliações  
- Adicionar do catálogo  
- Remover  
- Ordenar (SQL de swapOrdem)  
- Evitar duplicações  
- Tela dedicada

### ✔ Participações e Respostas  
- Estrutura de dados e serviços completos  
- API funcional (testada em Postman)

---

# <a name="entrega3"></a>8. 🚧 Pendências para Entrega 3 (React/JSP)

 
- Dashboards para Gestor/Admin  
- Relatórios SQL avançados  
  - média geral  
  - comparação entre unidades  
  - evolução temporal  
  - ranking por critério  
  - distribuição estatística  
- Gráficos com Chart.js ou React Charts  
- Exportações (CSV/PDF)

---

# <a name="estrutura"></a>9. 🧩 Estrutura Completa do Projeto

```
medimetrix/
├─ docs/
│  ├─ MER, Relacional, PDFs
│  └─ sql/
├─ src/main/java/com/mmx/medimetrix/
│  ├─ web/mvc/controller/
│  ├─ application/
│  ├─ infrastructure/dao/
│  └─ domain/core/
├─ src/main/resources/
│  ├─ templates/
│  ├─ fragments/
│  └─ db/migration/
├─ pom.xml
└─ README.md
```

---

# <a name="como-executar"></a>10. ▶ Como Executar

### 1. Criar banco:
```sql
CREATE DATABASE medimetrix;
```

### 2. Configurar `application.yml`  
Com PostgreSQL + Flyway habilitado.

### 3. Rodar via IntelliJ  
Classe:
```
MediMetrixApplication.java
```

### 4. Acessar:
```
http://localhost:8080
```

---

# <a name="autor"></a>11. 👤 Autor

**Adriano Lúcio Uchôa Brandão**  
Curso de Ciência de Dados e Inteligência Artificial – UEL  
Disciplina Banco de Dados I – Prof. Anderson Ávila Santos  

---

**Versão Estendida — Atualizada para a Entrega 2 (03/11/2025)**  
