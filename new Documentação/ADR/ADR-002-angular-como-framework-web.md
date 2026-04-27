# ADR-002: Escolha do Angular como Framework Web

**Data:** 2026-04-26  
**Status:** Aceito  
**Decisores:** Davi Cassoli Lira, Eder Henrique Pontes, Julia Cardoso, Daniel Godinho

---

## Contexto

O MyBuddy necessitava de um framework frontend para construir a interface web da plataforma. A escolha precisava levar em conta a familiaridade do time, a manutenibilidade do código a longo prazo e a integração com a stack já definida (Spring Boot + Keycloak). O principal concorrente avaliado foi o React, amplamente adotado no mercado.

---

## Decisão

Decidimos adotar o Angular 21 como framework web do MyBuddy. O projeto utiliza TypeScript nativo, lazy loading por módulo de feature, SSR via Angular Universal e integração com PrimeNG como biblioteca de componentes.

---

## Consequências

### Positivas

- Time já familiarizado com Angular, reduzindo o tempo de onboarding e rampup
- Estrutura opinada e organizada — convenções claras para serviços, componentes, guards e interceptors facilitam a manutenção e o code review
- TypeScript nativo sem configuração adicional, com suporte completo a tipagem estática desde o início
- Injeção de dependência built-in facilita testes unitários e integração com serviços como Keycloak

### Negativas

- Verbosidade do código — decorators, módulos e injeção de dependência exigem mais boilerplate comparado ao React com hooks

### Neutras

- Bundle size ligeiramente maior que React em aplicações pequenas, mas sem impacto relevante para o escopo do projeto
- Angular 21 introduz mudanças na API de signals e standalone components que exigem atenção ao seguir exemplos de versões anteriores

---

## Alternativas Consideradas

### Opção A: React

Framework baseado em componentes com ecossistema amplo e flexível.

**Motivo da rejeição:** Apesar da popularidade, o React exige mais decisões arquiteturais por parte do time (gerenciamento de estado, roteamento, estrutura de pastas). O time tinha maior familiaridade com Angular e o projeto se beneficia de uma estrutura mais opinada para garantir consistência entre os membros.

---

## Referências

- [Angular Documentation](https://angular.dev)
- [PrimeNG](https://primeng.org)
- [keycloak-angular](https://github.com/mauriciovigolo/keycloak-angular)
