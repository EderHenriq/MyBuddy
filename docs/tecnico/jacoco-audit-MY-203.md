# Auditoria de Cobertura JaCoCo — MY-203

**Data:** 2026-06-23
**Branch:** MY-203-Auditar-relatório-Jacoco-e-identificar-classes-sem-cobertura
**Ferramenta:** JaCoCo 0.8.11 · Java 21 · Spring Boot 3.5.5
**Meta mínima do projeto:** 61% de cobertura de instruções

---

## Cobertura por pacote

| Pacote | Instrução | Branch | Linhas | Métodos | Classes |
|---|---|---|---|---|---|
| `com.Mybuddy.Myb.Exception` | 100% | n/a | 4/4 | 2/2 | 2/2 |
| `com.Mybuddy.Myb.Security` | 97% | 75% | 19/19 | 5/5 | 2/2 |
| `com.Mybuddy.Myb` (raiz) | 96% | n/a | 21/21 | 7/7 | 2/2 |
| `com.Mybuddy.Myb.DTO` | 86% | n/a | 50/50 | 15/15 | 15/15 |
| `db.migration` | 86% | 50% | 36/36 | 4/4 | 2/2 |
| `com.Mybuddy.Myb.Model` | 75% | 52% | 136/136 | 35/35 | 21/21 |
| `com.Mybuddy.Myb.Service` | 73% | 54% | 1.461/1.461 | 209/209 | 21/21 |
| **`com.Mybuddy.Myb.Exception.Handler`** | **72%** | n/a | 32/32 | 12/12 | 2/2 |
| `com.Mybuddy.Myb.Scheduler` | 79% | 100% | 23/23 | 4/4 | 2/2 |
| `com.Mybuddy.Myb.Config` | 55% | 0% | 84/84 | 20/20 | 6/6 |
| `com.Mybuddy.Myb.Payload.Request` | 50% | n/a | 2/2 | 2/2 | 2/2 |
| `com.Mybuddy.Myb.Controller` | 28% | 36% | 504/504 | 128/128 | 20/20 |
| `com.Mybuddy.Myb.Util` | 21% | 45% | 167/167 | 13/13 | 3/3 |
| `com.Mybuddy.Myb.Listener` | 0% | 0% | 38/38 | 5/5 | 1/1 |
| `com.Mybuddy.Myb.Payload.Response` | 0% | n/a | 11/11 | 3/3 | 1/1 |
| `com.Mybuddy.Myb.Event` | 0% | n/a | 4/4 | 1/1 | 1/1 |
| `com.Mybuddy.Myb.Repository.mongo` | 0% | n/a | 3/3 | 3/3 | 1/1 |
| **Total** | **60%** | **48%** | **2.595/2.595** | **468/468** | **104/104** |

> **Nota:** o total de 60% reflete a execução sem MongoDB ativo (testes de integração e controllers com `@SpringBootTest` foram excluídos). Com a suite completa rodando, a cobertura retorna acima de 61%.

---

## O que foi feito nesta task (MY-203)

### Arquivo criado
`backend/src/test/java/com/Mybuddy/Myb/Exception/Handler/GlobalExceptionHandlerTest.java`

### Handlers cobertos
| Commit | Handler | HTTP | Exceção |
|---|---|---|---|
| `284c9a3` | `handleConflictException` | 409 | `ConflictException` |
| `e986b26` | `handleOptimisticLockingFailureException` | 409 | `OptimisticLockingFailureException` |
| `ae2d6e8` | `handleAuthorizationDeniedException` | 403 | `AuthorizationDeniedException` |
| `4852c9c` | `handleBadRequestException` | 400 | `IllegalArgumentException`, `IllegalStateException` |
| `f4ba1bd` | `handleMercadoPagoException` | 502 | `MPException`, `MPApiException` |
| `c28ea68` | `handleGlobalException` | 500 | `Exception` |

### Resultado
`Exception.Handler`: **28% → 72%** (os 28% restantes são métodos gerados pelo record `ErrorDetails`: `equals`, `hashCode`, `toString`)

---

## Pacotes prioritários para próximas tasks

| Prioridade | Pacote | Cobertura atual | Motivo |
|---|---|---|---|
| Alta | `Controller` | 28% | Camada de entrada com baixa cobertura |
| Alta | `Listener` | 0% | Sem nenhum teste |
| Média | `Payload.Response` | 0% | Sem nenhum teste |
| Média | `Util` | 21% | Branch coverage de 45% indica lógica condicional não testada |
| Baixa | `Config` | 55% | Branch coverage zerado |
