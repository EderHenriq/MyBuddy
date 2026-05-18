# ADR-004: Mercado Pago como Gateway de Pagamento

**Data:** 2026-05-17
**Status:** Aceito
**Decisores:** Davi Cassoli Lira, Eder Henrique Pontes, Julia Cardoso, Daniel Godinho

---

## Contexto

O MyBuddy necessita de um gateway de pagamento para processar taxas de adoção e doações para ONGs parceiras. A solução precisava suportar o mercado brasileiro, integrar-se com o backend Spring Boot, oferecer ambiente sandbox para testes e permitir split payment entre a plataforma e as ONGs no futuro.

A equipe realizou uma análise técnica prévia (MY-20) que incluiu leitura da documentação oficial da API v2, criação de contas de teste, testes do fluxo com Checkout Pro no sandbox, testes de recebimento de webhooks com ngrok e avaliação entre Payment Brick e Checkout Pro.

---

## Decisão

Decidimos adotar o Mercado Pago como gateway de pagamento do MyBuddy, utilizando o **Checkout Pro** via SDK Java (`mercadopago:sdk-java:2.9.2`). O backend Spring Boot cria preferências de pagamento via `PreferenceClient`, retorna o `init_point` para o frontend redirecionar o usuário ao checkout hospedado do MP, e processa notificações via webhook com validação de assinatura HMAC-SHA256.

O processamento de webhooks foi implementado com **Spring Events** para garantir resposta imediata ao MP (`200 OK`) e processamento assíncrono via `@Async`.

---

## Consequências

### Positivas

- Solução nativa para o mercado brasileiro com suporte a PIX, boleto, cartão de crédito e débito
- SDK Java oficial mantido pelo Mercado Pago com suporte ativo
- Checkout Pro hospedado pelo MP elimina a necessidade de implementar UI de pagamento
- Ambiente sandbox completo para testes sem custos
- Webhook com assinatura HMAC-SHA256 garante segurança nas notificações
- Suporte nativo a split payment para futura implementação entre plataforma e ONGs
- Reconhecimento de marca elevado — usuários brasileiros confiam no Mercado Pago

### Negativas

- Dependência de serviço externo — disponibilidade da API do MP afeta diretamente o fluxo de pagamento
- Sandbox com limitações: contas de teste não conseguem criar aplicações próprias, dificultando testes E2E completos
- `init_point` redireciona o usuário para fora da plataforma, quebrando a experiência de usuário no checkout
- Taxa de transação aplicada em produção

### Neutras

- O `pet` no pagamento é opcional — suporta tanto taxa de adoção vinculada a um pet quanto doações livres para ONGs
- Status do pagamento mapeado internamente: `approved`, `rejected`, `cancelled`, `refunded`, `pending`
- O `mpPreferenceId` é salvo no banco para correlacionar preferência com pagamento após o webhook

---

## Alternativas Consideradas

### Opção A: Stripe

Gateway internacional com excelente documentação e SDK robusto.

**Motivo da rejeição:** Suporte limitado a métodos de pagamento brasileiros (sem PIX, boleto nativo). Taxas em dólar e conversão cambial indesejável para um projeto voltado ao mercado brasileiro. Menor reconhecimento de marca entre usuários brasileiros.

### Opção B: Payment Brick (Mercado Pago)

SDK JavaScript que renderiza um formulário de pagamento embutido diretamente na página, sem redirecionar o usuário.

**Motivo da rejeição:** Requer implementação de UI de pagamento no frontend Angular, aumentando a complexidade. O Checkout Pro (redirecionamento) foi avaliado na MY-151 como mais adequado para o estágio atual do projeto — menos código, menos surface de ataque, e manutenção delegada ao MP.

### Opção C: PagSeguro

Gateway brasileiro alternativo com suporte a métodos locais.

**Motivo da rejeição:** SDK Java desatualizado, documentação inferior e menor adoção no mercado comparado ao Mercado Pago. Sem suporte oficial a split payment.

---

## Referências

- [Mercado Pago Developers](https://www.mercadopago.com.br/developers)
- [Checkout Pro Documentation](https://www.mercadopago.com.br/developers/pt/docs/checkout-pro/landing)
- [Webhooks MP — Validação de assinatura](https://www.mercadopago.com.br/developers/pt/docs/your-integrations/notifications/webhooks)
- [SDK Java — mercadopago:sdk-java](https://github.com/mercadopago/sdk-java)
- MY-20: Pesquisa e decisão técnica sobre integração MP (concluído)
- MY-21: Implementação do fluxo de pagamento (Sprint 1)