# 🩺 MediMetrix (MMX)
**Sistema de Avaliação de Desempenho Clínico**

O **MediMetrix** é uma aplicação desenvolvida em **Java Spring Boot** com o objetivo de oferecer uma plataforma para avaliação, registro e análise de desempenho clínico, apoiando práticas de melhoria contínua e feedback ético entre profissionais da saúde.

---

## 📂 Estrutura do Projeto

```
medimetrix/
├─ docs/                          → Documentos do projeto (ex.: MediMetrix_Projeto_BD1.md)
├─ src/
│  ├─ main/
│  │  ├─ java/com/mmx/medimetrix/ → Código-fonte (controllers, services, DAO)
│  │  └─ resources/
│  │     ├─ db.migration/         → Scripts SQL do Flyway (migrar schema e seeds)
│  │     ├─ static/               → Arquivos estáticos (JS, CSS, imagens)
│  │     ├─ templates.relatorios/ → Templates Thymeleaf para relatórios
│  │     └─ application.yml       → Configurações do Spring Boot
│  └─ test/java/                  → Testes automatizados (JUnit)
├─ pom.xml                        → Dependências e build Maven
├─ .gitignore                     → Regras de versionamento
└─ README.md                      → Este arquivo
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
