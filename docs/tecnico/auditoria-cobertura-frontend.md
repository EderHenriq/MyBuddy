# Auditoria de Cobertura de Testes — Frontend Angular

**Task:** MYB-206  
**Data:** 2026-06-26  
**Framework de testes:** Vitest ^4.0.8 + Angular TestBed  
**Ferramenta de cobertura:** @vitest/coverage-istanbul (Istanbul/nyc)

---

## Como executar o relatório de cobertura

```bash
cd frontend
npm run test:coverage
# Relatório HTML gerado em: frontend/coverage/index.html
```

---

## Resumo Executivo

| Categoria         | Total | Com testes | Sem testes | Cobertura |
|------------------|-------|------------|------------|-----------|
| Componentes      |  18   |     4      |    14      |   22%     |
| Serviços         |  16   |     2      |    14      |   12%     |
| Diretivas        |   2   |     0      |     2      |    0%     |
| **Total geral**  | **36**| **6**      |  **30**    | **17%**   |

> Cobertura estimada por contagem de unidades (spec files). A cobertura real por linhas
> será gerada ao executar `npm run test:coverage`.

---

## Classes COM cobertura (6)

### Componentes
| Arquivo | Spec | Qualidade |
|---------|------|-----------|
| `shared/components/card-pet/card-pet.component.ts` | card-pet.spec.ts | Básico (1 teste) |
| `shared/components/footer/footer.component.ts` | footer.spec.ts | Básico (1 teste) |
| `shared/components/header-landing-page/header-landing-page.component.ts` | header-landing-page.spec.ts | Básico (1 teste) |
| `shared/components/header-main/header-main.component.ts` | header-main.spec.ts | Básico (1 teste) |

### Serviços
| Arquivo | Spec | Qualidade |
|---------|------|-----------|
| `core/services/pet.service.ts` | pet.service.spec.ts | 2 testes (criação + HTTP GET) |
| `core/services/user.service.ts` | user.service.spec.ts | 2 testes (criação + HTTP GET) |

---

## Classes SEM cobertura (30)

### Componentes sem spec (14)

| Arquivo | Complexidade | Prioridade |
|---------|-------------|------------|
| `shared/components/paginator/paginator.component.ts` | Média (lógica de paginação) | **Alta** |
| `shared/components/card-produto/card-produto.component.ts` | Baixa (inputs/outputs) | **Alta** |
| `shared/components/search-bar/search-bar.component.ts` | Baixa (emit on search) | **Alta** |
| `shared/components/modal/modal.component.ts` | Baixa (dialog events) | **Alta** |
| `shared/components/btn-outline/btn-outline.component.ts` | Baixa (variant/size) | **Alta** |
| `shared/components/chip-filtro/chip-filtro.component.ts` | Baixa (click emit) | Média |
| `shared/components/card-avaliacao/card-avaliacao.component.ts` | Baixa | Média |
| `shared/components/card-categoria/card-categoria.component.ts` | Baixa | Média |
| `shared/components/card-evento/card-evento.component.ts` | Baixa | Média |
| `shared/components/card-loja/card-loja.component.ts` | Baixa | Média |
| `shared/components/card-servico/card-servico.component.ts` | Baixa | Média |
| `shared/components/cart-drawer/cart-drawer.component.ts` | Média | Baixa |
| `shared/components/category-carousel/category-carousel.component.ts` | Baixa | Baixa |
| `shared/components/hero-section/hero-section.component.ts` | Baixa | Baixa |
| `shared/components/banner-lembrete/banner-lembrete.component.ts` | Baixa | Baixa |

### Serviços sem spec (14)

| Arquivo | Complexidade | Prioridade |
|---------|-------------|------------|
| `core/services/cart.service.ts` | Média (signals + CRUD carrinho) | **Alta** |
| `core/services/loading.service.ts` | Baixa (request counter) | **Alta** |
| `core/services/session.service.ts` | Baixa (localStorage + BehaviorSubject) | **Alta** |
| `core/services/api.service.ts` | Baixa (HTTP wrapper) | **Alta** |
| `core/services/notification.service.ts` | Alta (mock por papel) | Média |
| `core/services/produto.service.ts` | Alta (HTTP + localStorage fallback) | Média |
| `core/services/auth.service.ts` | Alta (Keycloak + mock) | Média |
| `core/services/donation.service.ts` | Média | Baixa |
| `core/services/mercadopago.service.ts` | Alta (SDK externo) | Baixa |
| `core/services/ong.service.ts` | Baixa (HTTP) | Baixa |
| `core/services/pedido.service.ts` | Média | Baixa |
| `core/services/petshop.service.ts` | Baixa | Baixa |
| `core/services/upload.service.ts` | Baixa | Baixa |
| `core/services/admin.service.ts` | Baixa | Baixa |

### Diretivas sem spec (2)

| Arquivo | Complexidade | Prioridade |
|---------|-------------|------------|
| `shared/directives/debounce.directive.ts` | Média | Baixa |
| `shared/directives/infinite-scroll.directive.ts` | Média | Baixa |

---

## Configuração adicionada (MYB-206)

### `angular.json` — seção `test`
Habilitado `codeCoverage: true` com exclusões para arquivos de bootstrap e ambiente.

### `package.json`
- Script adicionado: `"test:coverage": "ng test --code-coverage --watch=false"`
- Dependência adicionada: `"@vitest/coverage-istanbul": "^4.0.8"`

> **Ação necessária:** executar `npm install` para instalar `@vitest/coverage-istanbul`.

---

## Meta de cobertura

| Task | Meta | Status |
|------|------|--------|
| MYB-206 | Auditoria e configuração | ✓ Concluído |
| MYB-208 | 50% de cobertura de linhas | ⏳ Pendente |
