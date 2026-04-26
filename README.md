<div align="center">

# MyBuddy 🐾

### O HUB completo do ecossistema pet local

MyBuddy é uma plataforma web que centraliza **adoção, serviços, eventos e marketplace pet** em um único lugar - conectando adotantes, ONGs, protetores, pet shops e clínicas veterinárias.

<img src="https://ww2.kqed.org/pop/wp-content/uploads/sites/12/2016/11/unusual-animal-friendships-cute-gifs-8__605.gif" alt="gifpet" width="300" />

</div>

---

##  O Problema que Resolvemos

ONGs e protetores **lutam por visibilidade** em redes sociais desordenadas. Adotantes se perdem em **buscas ineficientes**. Tutores não encontram **serviços locais confiáveis**. O MyBuddy conecta todas essas pontas em um único ecossistema.

---

##  Funcionalidades

| Módulo | Descrição |
|---|---|
| 🐶 **Portal de Adoção** | Vitrine de animais com perfis detalhados e filtros por espécie, porte e localização |
| 📅 **Feiras de Adoção** | Cadastro e divulgação de eventos locais por ONGs e protetores |
| 🗺️ **Guia de Serviços** | Diretório geolocalizado de pet shops e clínicas veterinárias |
| 🏪 **Marketplace** | Compra e venda de produtos e serviços pet com pagamento integrado |
| 💰 **Doações** | Canal direto de apoio financeiro para ONGs e protetores cadastrados |
| 🔐 **Autenticação** | Login federado via Keycloak com perfis para adotante, ONG e pet shop |

---

## Setup do Ambiente

### Pré-requisitos

- Docker e Docker Compose instalados
- Portas disponíveis: 80, 8080, 8081, 5432, 27017

### Subindo o ambiente completo

```bash
# 1. Clone o repositório
git clone https://github.com/EderHenriq/MyBuddy.git
cd MyBuddy

# 2. Configure as variáveis de ambiente
cp .env.example .env
# Edite o .env com suas configurações

# 3. Suba todos os serviços
docker compose up --build
```

Aguarde todos os containers ficarem `(healthy)`. A ordem de subida é garantida automaticamente:

```
PostgreSQL + MongoDB → Keycloak → Backend → Frontend
```

### Serviços disponíveis

| Serviço | URL |
|---|---|
| Frontend Angular | http://localhost |
| Backend API | http://localhost:8081 |
| Keycloak Admin | http://localhost:8080 |
| Health Check | http://localhost:8081/actuator/health |

### Comandos úteis

```bash
# Ver status dos containers
docker compose ps

# Ver logs de um serviço
docker logs mybuddy-backend --tail 50

# Parar todos os serviços
docker compose down

# Parar e remover volumes (reset completo)
docker compose down -v
```

### Observações

- O realm `mybuddy` do Keycloak é importado automaticamente na primeira subida via `docker/keycloak/realm-export.json`
- O banco `keycloak` é criado automaticamente via `docker/init-db.sql`
- Dados do PostgreSQL e MongoDB persistem entre reinicializações via volumes Docker

---

##  Stack Tecnológica

<div align="center">

**Backend**

<img src="https://img.shields.io/badge/Java%2021-007396?style=for-the-badge&logo=java&logoColor=white" alt="Java"/>
<img src="https://img.shields.io/badge/Spring%20Boot%203-6DB33F?style=for-the-badge&logo=spring&logoColor=white" alt="Spring Boot"/>
<img src="https://img.shields.io/badge/Keycloak-4D4D4D?style=for-the-badge&logo=keycloak&logoColor=white" alt="Keycloak"/>
<img src="https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white" alt="Maven"/>

**Banco de Dados**

<img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="MongoDB"/>
<img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL"/>

**Frontend Web**

<img src="https://img.shields.io/badge/Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular"/>
<img src="https://img.shields.io/badge/TypeScript-3178C6?style=for-the-badge&logo=typescript&logoColor=white" alt="TypeScript"/>

**Mobile**

<img src="https://img.shields.io/badge/Flutter-02569B?style=for-the-badge&logo=flutter&logoColor=white" alt="Flutter"/>
<img src="https://img.shields.io/badge/Dart-0175C2?style=for-the-badge&logo=dart&logoColor=white" alt="Dart"/>

**Pagamentos & Infra**

<img src="https://img.shields.io/badge/Mercado%20Pago-009EE3?style=for-the-badge&logo=mercadopago&logoColor=white" alt="Mercado Pago"/>
<img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker"/>
<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" alt="GitHub"/>
<img src="https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white" alt="Jira"/>

</div>


> **MongoDB** é o banco principal da plataforma (adoção, usuários, eventos, serviços).
> **PostgreSQL** é utilizado especificamente no módulo Marketplace, dado o modelo relacional de produtos, pedidos e transações financeiras.

---

##  Estrutura do Repositório

```
MyBuddy/
├── Back End/                 # Java 21 + Spring Boot 3 + Keycloak + Maven
├── Web HTML/                 # Frontend legado HTML/CSS/JS
├── new Frontend - Angular/   # Migração para Angular (em andamento)
├── Banco de dados/           # Scripts SQL e migrations
├── Documentação/             # Documentação acadêmica e técnica
└── new Documentação/         # Documentação atualizada e roadmap
```

---

## 👥 Time

| Nome | RA |
|---|---|
| Eder Henrique Pontes | 24534211-2 |
| Julia Cardoso | 24503170-2 |
| Davi Cassoli Lira | 24042075-2 |
| Daniel Godinho | 24383624-2 |

> Projeto de Software — UniCesumar, Maringá-PR, 2026.

---

<div align="center">

[![Jira](https://img.shields.io/badge/Acompanhe%20no%20Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white)](https://projetodesoftware420.atlassian.net/jira/software/projects/BUDDY/boards/34)

</div>
