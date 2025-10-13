# 🩺 MediMetrix (MMX)
**Sistema de Avaliação de Desempenho Clínico**

O **MediMetrix** é uma aplicação desenvolvida em **Java Spring Boot** com o objetivo de oferecer uma plataforma para avaliação, registro e análise de desempenho clínico, apoiando práticas de melhoria contínua e feedback ético entre profissionais da saúde.

---

## ✅ Entrega 1 — Itens atendidos

- **Descrição detalhada do sistema e fluxos funcionais:** `docs/MediMetrix_Descricao_Sistema.pdf`
- **Modelagem conceitual (Diagrama ER):** `docs/MediMetrix - MER.png`
- **Esquema relacional normalizado (mapeado p/ PostgreSQL):** `docs/MediMetrix - Esquema Relacional.md`
- **Script SQL de criação do banco (com PK/FK/UNIQUE/CHECK/índices):** `docs/sql/` (arquivos 00–14)
- **Protótipos visuais dos relatórios (gráficos/tabelas):** incluídos no PDF de descrição
- **Explicação técnica das consultas SQL dos relatórios:** incluída no PDF de descrição

> Observação: o documento “**MediMetrix_Projeto_BD1.md**” agrega o racional do projeto, contexto e referências.



## 📂 Estrutura do Projeto

```
medimetrix/
├─ docs/
│  ├─ MediMetrix_Projeto_BD1.md
│  ├─ MediMetrix_Descricao_Sistema.pdf          # descrição, fluxos, mockups e consultas SQL explicadas
│  ├─ MediMetrix - MER.png                      # diagrama ER
│  ├─ MediMetrix - Esquema Relacional.md        # tabelas (3FN) e chaves
│  └─ sql/                                      # DDL PostgreSQL (ordem 00 → 14)
│     ├─ 00_create_database.sql                 # master: cria DB e encadeia demais
│     ├─ 01_create_schema_basico.sql
│     ├─ 02_create_questao.sql
│     ├─ 03_create_criterio.sql
│     ├─ 04_create_avaliacao.sql
│     ├─ 05_create_avaliacao_questao.sql
│     ├─ 06_create_meta.sql
│     ├─ 07_create_participacao.sql
│     ├─ 08_create_resposta.sql
│     ├─ 09_create_unidade.sql
│     ├─ 10_create_especialidade.sql
│     ├─ 11_create_usuario.sql
│     ├─ 12_create_medico.sql
│     ├─ 13_create_gestor.sql
│     └─ 14_create_admin.sql
├─ src/
│  ├─ main/java/com/mmx/medimetrix/             # controllers, services, dao
│  └─ main/resources/
│     ├─ templates/                             # Thymeleaf
│     └─ application.yml
├─ pom.xml
└─ README.md
```

---


## ⚙️ Tecnologias Principais

| Categoria | Tecnologia / Biblioteca |
|------------|--------------------------|
| Linguagem | **Java 17** |
| Framework | **Spring Boot 3.5.6** |
| Front-end | **Thymeleaf + Chart.js** |
| Banco de Dados | **PostgreSQL** (a ser configurado) |
| Migração de BD | **Flyway** |
| Build / Gestão | **Maven** |
| IDE Recomendada | **IntelliJ IDEA (Community ou Ultimate)** |

---

## 🚀 Como Rodar sem Banco (temporariamente)

Enquanto o PostgreSQL ainda não estiver instalado, o projeto pode ser executado sem configurar o `DataSource`.  
O arquivo `application.yml` vem com a auto-configuração de banco desabilitada:

```yaml
spring:
  autoconfigure:
    exclude:
      - org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
  thymeleaf:
    cache: false

server:
  port: 8080
```

### Para testar
1. Rode a classe `MediMetrixApplication` no IntelliJ (botão ▶️).
2. Acesse **http://localhost:8080/**
3. Deve aparecer a mensagem “MediMetrix rodando! (sem banco por enquanto)”.

---

## 🧩 Próximos Passos

Após instalar o **PostgreSQL**:
1. Criar o banco de dados e o usuário:
   ```sql
   CREATE DATABASE medimetrix_db;
   CREATE USER medimetrix_admin WITH ENCRYPTED PASSWORD 'change-me';
   GRANT ALL PRIVILEGES ON DATABASE medimetrix_db TO medimetrix_admin;
   ```
2. Atualizar o `application.yml` para:
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/medimetrix_db
       username: medimetrix_admin
       password: change-me
     thymeleaf:
       cache: false
     flyway:
       enabled: true
       locations: classpath:db/migration
   server:
     port: 8080
   ```
3. Adicionar os scripts:
    - `V1__schema_inicial.sql` → criação das tabelas.
    - `V2__seed_minima.sql` → dados iniciais (especialidades, critérios etc.).
4. Rodar novamente a aplicação → o **Flyway** criará as tabelas automaticamente.

---

## 📘 Documentação do Projeto

O documento principal do projeto está em:

```
docs/MediMetrix_Projeto_BD1.md
```

Ele contém:
- Descrição geral do sistema
- Modelo conceitual e relacional
- Escopo dos relatórios
- Políticas de sigilo (Aberto, Confidencial, Anônimo)
- Justificativa e tecnologias utilizadas

---

## 🧠 Créditos

**Autor:** Adriano Lúcio Uchôa Brandão  
**Professor:** Anderson Paulo Ávila Santos  
**Disciplina:** Banco de Dados I – Curso de Ciência de Dados e Inteligência Artificial – UEL  
**Ano:** 2025

---

> 💡 *Versão inicial do projeto sem banco de dados configurado. Estrutura base criada para posterior integração com PostgreSQL e Flyway.*
