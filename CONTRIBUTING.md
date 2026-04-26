# Guia de Contribuição — MyBuddy

Obrigado por contribuir com o MyBuddy! Este guia explica como colaborar de forma consistente com o projeto.

---

## Pré-requisitos

- Docker e Docker Compose
- Java 21
- Node.js 22+
- Angular CLI
- Maven

---

## Configurando o ambiente local

```bash
# 1. Clone o repositório
git clone https://github.com/EderHenriq/MyBuddy.git
cd MyBuddy

# 2. Configure as variáveis de ambiente
cp .env.example .env
# Edite o .env com suas configurações locais

# 3. Suba a infraestrutura
docker compose up -d postgres mongodb keycloak

# 4. Rode o backend
cd "Back End/JavaSpring/Myb Def/backend-mybuddy"
./mvnw spring-boot:run

# 5. Rode o frontend
cd "Front End - Angular/mybuddy"
npm install
ng serve
```

---

## Fluxo de trabalho com Git

### Branches

O projeto segue o modelo:

```
main        → produção
Developer   → integração (branch base para PRs)
feature/*   → novas funcionalidades
fix/*       → correções de bug
hotfix/*    → correções urgentes em produção
```

Sempre crie sua branch a partir da `Developer`:

```bash
git checkout Developer
git pull origin Developer
git checkout -b feat/MY-XXX-descricao-curta
```

### Padrão de branch

```
<tipo>/MY-<número>-<descricao-curta>
```

Exemplos:
```
feat/MY-42-filtro-especie-animais
fix/MY-87-corrige-login-keycloak
hotfix/MY-101-token-expirado
```

---

## Convenção de Commits

O projeto usa [Conventional Commits](https://www.conventionalcommits.org/) com referência obrigatória ao card do Jira.

### Estrutura

```
<tipo>(escopo): <descrição curta>  [MY-XXX] #<ação-jira>
```

### Tipos

| Tipo | Quando usar |
|------|-------------|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `hotfix` | Correção urgente em produção |
| `refactor` | Melhoria sem alterar comportamento |
| `chore` | Manutenção, deps, configs |
| `docs` | Documentação |
| `test` | Testes |
| `style` | Formatação sem lógica |
| `perf` | Performance |
| `ci` | CI/CD |

### Escopos do monorepo

| Escopo | Módulo |
|--------|--------|
| `backend` | Java / Spring Boot |
| `frontend` | Angular |
| `mobile` | Flutter |
| `auth` | Keycloak / JWT |
| `infra` | Docker, CI |
| `db` | Migrations |

### Exemplos

```bash
# Feature
git commit -m "feat(frontend): adiciona filtro por espécie na vitrine  [MY-55] #done"

# Bug fix
git commit -m "fix(auth): corrige refresh token expirado  [MY-88] #done"

# Chore
git commit -m "chore: atualiza dependências do Angular  [MY-60]"
```

### Smart Commits (Jira)

| Ação | Comando |
|------|---------|
| Mover para em progresso | `#in-progress` |
| Concluir | `#done` |
| Registrar tempo | `#time 2h 30m` |

---

## Abrindo um Pull Request

1. Certifique-se de que os testes passam localmente
2. Crie o PR apontando para a branch `Developer`
3. Preencha o template de PR obrigatório
4. Aguarde ao menos uma aprovação antes de mergear
5. Não faça squash de commits — mantenha o histórico

---

## Rodando os testes

```bash
# Backend
cd "Back End/JavaSpring/Myb Def/backend-mybuddy"
./mvnw test

# Frontend
cd "Front End - Angular/mybuddy"
ng test
```

O CI roda automaticamente em todo push para `Developer`, `main` e branches `feature/*`.

---

## Estrutura do repositório

```
MyBuddy/
├── Back End/                          # Java 21 + Spring Boot 3
├── Front End - Angular/mybuddy/       # Angular 21
├── docker-compose.yml                 # Ambiente completo
├── .env.example                       # Variáveis de ambiente
├── docker/
│   ├── init-db.sql                    # Criação do banco keycloak
│   └── keycloak/realm-export.json     # Realm mybuddy pré-configurado
└── .github/
    ├── workflows/ci.yml               # Pipeline de testes
    ├── PULL_REQUEST_TEMPLATE.md       # Template de PR
    ├── COMMIT_CONVENTION.md           # Guia de commits
    └── ISSUE_TEMPLATE/                # Templates de issue
```

---

## Dúvidas

Abra uma issue ou entre em contato pelo canal do time no Jira.
