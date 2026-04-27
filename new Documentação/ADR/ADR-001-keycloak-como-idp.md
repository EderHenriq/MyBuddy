# ADR-001: Escolha do Keycloak como Identity Provider

**Data:** 2026-04-26  
**Status:** Aceito  
**Decisores:** Davi Cassoli Lira, Eder Henrique Pontes, Julia Cardoso, Daniel Godinho

---

## Contexto

O MyBuddy necessita de um mecanismo de autenticação e autorização para proteger seus endpoints REST e gerenciar diferentes perfis de usuário (adotante, ONG, pet shop). A solução precisava suportar múltiplos roles, integrar-se com o backend Spring Boot e o frontend Angular, e seguir padrões modernos de segurança sem exigir que a equipe implementasse e mantivesse toda a lógica de autenticação manualmente.

A alternativa mais imediata seria implementar Spring Security clássico com geração e validação de JWT manual — abordagem que a equipe já conhecia e que é amplamente documentada. Porém, essa abordagem implica em manter código sensível de segurança dentro da aplicação, lidar com rotação de tokens, gerenciamento de sessões e controle de roles manualmente.

---

## Decisão

Decidimos adotar o Keycloak 26 como Identity Provider centralizado para o MyBuddy. O backend Spring Boot foi configurado como OAuth2 Resource Server, validando tokens JWT emitidos pelo Keycloak via `spring-boot-starter-oauth2-resource-server`. O frontend Angular utiliza `keycloak-angular` para gerenciar o fluxo de login, logout e injeção do Bearer token nas requisições.

---

## Consequências

### Positivas

- Autenticação e autorização delegadas a um servidor dedicado, eliminando código sensível de segurança na aplicação
- Suporte nativo a OAuth2/OIDC, padrão de mercado amplamente adotado em aplicações enterprise
- Gerenciamento de roles, usuários e sessões via console administrativo sem necessidade de código adicional
- Integração direta com Spring Boot via `issuer-uri`, sem necessidade de configurar validação de assinatura manualmente
- SSO disponível nativamente caso o ecossistema MyBuddy cresça com novos serviços

### Negativas

- Curva de aprendizado elevada — configuração de realms, clients, scopes e flows exige familiarização prévia com os conceitos do Keycloak
- Complexidade de setup inicial maior comparado ao Spring Security clássico
- Adiciona um serviço externo obrigatório na infraestrutura — ambiente de desenvolvimento requer o Keycloak rodando via Docker

### Neutras

- O fluxo de cadastro de usuários ainda é feito pelo backend (`/api/auth/cadastro`), sincronizando com o Keycloak via `KeycloakUserSyncService`
- Em produção, o Keycloak precisará de configuração adicional para HTTPS e hostname adequado

---

## Alternativas Consideradas

### Opção A: Spring Security clássico com JWT manual

Implementação de `UserDetailsService`, `AuthenticationManager`, geração de JWT via JJWT e validação manual de tokens.

**Motivo da rejeição:** Maior superfície de ataque por manter lógica de segurança dentro da aplicação. Exige manutenção contínua de rotação de chaves, blacklist de tokens e gerenciamento de sessões. Não segue os padrões OAuth2/OIDC modernos e dificulta a evolução para SSO no futuro.

### Opção B: Auth0

Serviço gerenciado de autenticação com plano gratuito limitado.

**Motivo da rejeição:** Dependência de serviço externo pago em escala, sem controle sobre os dados dos usuários. Não adequado para um projeto acadêmico com necessidade de ambiente 100% local e sem custos.

---

## Referências

- [Keycloak Documentation](https://www.keycloak.org/documentation)
- [Spring Boot OAuth2 Resource Server](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/index.html)
- [keycloak-angular](https://github.com/mauriciovigolo/keycloak-angular)
