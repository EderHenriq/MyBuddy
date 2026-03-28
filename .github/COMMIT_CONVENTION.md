# Convenção de Commits — MyBuddy

Este projeto adota o padrão **[Conventional Commits](https://www.conventionalcommits.org/)** combinado com **Smart Commits do Jira** para rastreabilidade total entre código e tarefas.

---

## Estrutura do Commit

```
<tipo>(escopo): <descrição curta>  [MYB-XXX] #<ação-jira>

[corpo opcional]

[rodapé opcional]
```

### Exemplo real:

```
feat(backend): adiciona filtro por espécie no endpoint de animais  [MYB-42] #in-progress

Implementa query param `species` no GET /animals.
Aceita valores: dog, cat, bird, other.

Closes: MYB-42
```

---

## Tipos de Commit

| Tipo | Quando usar | Exemplo |
|------|-------------|---------|
| `feat` | Nova funcionalidade | `feat(frontend): adiciona tela de perfil do animal` |
| `fix` | Correção de bug | `fix(backend): corrige NPE no serviço de adoção` |
| `hotfix` | Correção urgente em produção | `hotfix(auth): revoga tokens expirados indevidamente` |
| `refactor` | Melhoria de código sem mudar comportamento | `refactor(mobile): extrai widget de card de animal` |
| `chore` | Tarefas de manutenção, deps, configs | `chore: atualiza versão do Spring Boot para 3.3` |
| `docs` | Documentação | `docs: atualiza README com instruções de setup` |
| `test` | Adição ou correção de testes | `test(backend): adiciona testes unitários para AdoptionService` |
| `style` | Formatação, espaçamento (sem lógica) | `style(frontend): aplica lint no módulo de eventos` |
| `perf` | Melhoria de performance | `perf(backend): adiciona índice na tabela animals` |
| `ci` | Configuração de CI/CD | `ci: adiciona workflow de build no GitHub Actions` |
| `revert` | Reverte um commit anterior | `revert: feat(frontend): tela de perfil do animal` |

---

## Escopos do Monorepo

Use o escopo para indicar qual parte do projeto foi alterada:

| Escopo | Módulo |
|--------|--------|
| `backend` | API Java / Spring Boot |
| `frontend` | Angular |
| `mobile` | Flutter |
| `auth` | Autenticação / JWT / Keycloak |
| `infra` | Docker, CI, configurações |
| `db` | Migrations, schemas |

---

## Integração com Jira

### Padrão de Branch

```
<tipo>/MYB-<número>-<descricao-curta>
```

```bash
# Exemplos:
feat/MYB-42-filtro-especie-animais
fix/MYB-87-corrige-login-keycloak
hotfix/MYB-101-token-expirado
```

### Smart Commits (menção no corpo do commit)

Adicione ao final da mensagem para mover o card automaticamente no Jira:

| Ação | Comando | Resultado |
|------|---------|-----------|
| Mover para "Em progresso" | `#in-progress` | Card vai para coluna "In Progress" |
| Registrar tempo | `#time 2h 30m` | Lança horas na task |
| Adicionar comentário | `#comment <texto>` | Posta comentário no card |
| Resolver task | `#done` | Move card para "Done" |

> Smart Commits exigem que o **e-mail do GitHub** seja o mesmo cadastrado no Jira.

---

## Boas Práticas

- **Commits atômicos:** um commit = uma mudança lógica
- **Imperativo no presente:** `adiciona`, `corrige`, `remove` — não `adicionado` ou `adicionando`
- **Limite de 72 caracteres** na primeira linha
- **Sem ponto final** na descrição curta
- Referencie sempre o card Jira: `[MYB-XXX]`

---

## Exemplos Ruins

```bash
# Vago demais
git commit -m "fix: ajustes"

# Sem referência Jira
git commit -m "feat(backend): novo endpoint de pets"

# Passado / gerúndio
git commit -m "feat: adicionando tela de login"

# Múltiplas responsabilidades
git commit -m "feat: tela de perfil + fix no botão + chore: atualiza deps"
```

---

## Exemplos Bons

```bash
# Feature simples
git commit -m "feat(frontend): adiciona card de exibição do animal na vitrine  [MYB-55] #in-progress"

# Bug fix com contexto
git commit -m "fix(auth): corrige refresh token expirado antes do prazo  [MYB-88] #done"

# Chore sem escopo específico
git commit -m "chore: atualiza dependências do Angular para v19  [MYB-60]"

# Com corpo explicativo
git commit -m "refactor(backend): extrai lógica de geolocalização para service próprio  [MYB-71]

Move cálculo de distância do PetShopController para GeoService.
Facilita reuso no módulo de eventos e futura integração com Google Maps API."
```
