# Relatório de Auditoria e Prontidão para Produção: Banco de Dados MyBuddy

Realizamos um pente-fino minucioso no backend (Spring Boot), frontend (Angular), mobile (Flutter) e nos bancos de dados (PostgreSQL e MongoDB) do MyBuddy. O objetivo foi validar a integridade das ligações de dados, cobertura de índices, redundâncias e prontidão para o ambiente de produção.

---

## 1. Avaliação do Modelo Híbrido (PostgreSQL ⬌ MongoDB)

A divisão entre **PostgreSQL** (dados transacionais e estruturados) e **MongoDB** (documentos flexíveis e perfis) é conceitualmente correta e adequada para o ecossistema do MyBuddy:

* **PostgreSQL (Transacional):** Gerencia `Petshop`, `Produto`, `Pedido`, `ItemPedido`, `Cupom`, `Agendamento`, `Servico`, `CampanhaDoacao` e `Payment` (controle financeiro e auditoria).
* **MongoDB (Flexível):** Gerencia `Usuario` (Keycloak Integration), `Pet`, `Organizacao` (ONGs), `InteresseAdocao`, `Chat` e `EventoOng`.

### ⚠️ Risco Crítico: Integridade Referencial Inexistente (Banco ⬌ Banco)
Como os bancos são fisicamente isolados, **não existem chaves estrangeiras (FK) físicas** entre tabelas relacionais e coleções do MongoDB.
* Por exemplo: `pedidos.cliente_id` (PostgreSQL) aponta para um `Usuario.id` (MongoDB) e `agendamentos.pet_id` (PostgreSQL) aponta para um `Pet.id` (MongoDB).
* **Impacto:** Se um `Usuario` ou `Pet` for excluído do MongoDB, o PostgreSQL ficará com registros órfãos, gerando exceções de ponteiro nulo (`NullPointerException`) ou dados corrompidos quando o backend tentar renderizar históricos ou relatórios.
* **Recomendação:** Implementar **soft delete** (exclusão lógica por campo `ativo = false` ou `deletado_em`) em todas as coleções do MongoDB em vez de exclusão física, protegendo o histórico do PostgreSQL.

---

## 2. Cobertura de Índices e Performance

Encontramos diversas queries cruciais executadas pelo Backend que **não possuem índices correspondentes no banco**. Em ambiente de produção com milhares de registros, isso resultará em varreduras sequenciais completas (**Seq Scans** no Postgres e **COLLSCAN** no MongoDB), degradando o tempo de resposta das telas e ultrapassando o limite não-funcional de 5 segundos.

### A. PostgreSQL: Índices Faltantes

| Tabela | Coluna | Tipo de Índice | Caso de Uso Otimizado |
| :--- | :--- | :--- | :--- |
| `payments` | `mp_payment_id` | `B-Tree` | Webhooks do Mercado Pago buscando pagamentos por ID externo em tempo real. |
| `payments` | `usuario_id` | `B-Tree` | Tela de histórico de transações / doações do usuário logado. |
| `payments` | `pedido_id` | `B-Tree` | Verificação do status de pagamento de um pedido do Marketplace. |
| `payments` | `campanha_id` | `B-Tree` | Soma total arrecadada em campanhas de doação. |
| `pedidos` | `cliente_id` | `B-Tree` | Listagem de pedidos no perfil do cliente (Angular/Flutter). |
| `pedidos` | `petshop_id` | `B-Tree` | Painel Administrativo do Petshop listando suas vendas. |
| `pedidos` | `status` | `B-Tree` | Filtro de pedidos pendentes para rotinas automáticas de cancelamento. |
| `produtos` | `petshop_id` | `B-Tree` | Carregamento de catálogo por Petshop parceiro. |
| `produtos` | `subcategoria_id` | `B-Tree` | Filtros de navegação por categorias no Marketplace. |
| `servicos` | `petshop_id` | `B-Tree` | Exibição da lista de serviços oferecidos por um Petshop no checkout. |
| `agendamentos` | `cliente_id` | `B-Tree` | Meus agendamentos / Histórico do tutor do pet. |
| `agendamentos` | `pet_id` | `B-Tree` | Validação de conflito de agenda para o mesmo animal. |
| `agendamentos` | `(data_hora, data_hora_fim)` | `Composite B-Tree` | Busca de sobreposição de horários para profissionais e pets (validação de agenda). |
| `cupons` | `petshop_id` | `B-Tree` | Listagem e aplicação de cupons vinculados a petshops específicos. |
| `cupons_usuarios` | `pedido_id` | `B-Tree` | Rastreamento de qual pedido consumiu determinado cupom. |

### B. MongoDB: Índices Faltantes

| Coleção | Campo | Tipo de Índice | Caso de Uso Otimizado |
| :--- | :--- | :--- | :--- |
| `pets` | `organizacao` | `{ organizacao: 1 }` | Tela de lista de animais de uma determinada ONG (Dashboard da ONG). |
| `usuarios` | `petshopId` | `{ petshopId: 1 }` | Vinculação de usuários administradores ao seu respectivo Petshop. |
| `organizacoes`| `cnpj` | `{ cnpj: 1 } (Unique)` | Garantia física de que não haverá duplicidade de ONGs com o mesmo CNPJ. |
| `interesses_adocao` | `usuario` | `{ usuario: 1 }` | Listagem de intenções de adoção feitas por um adotante. |
| `interesses_adocao` | `pet` | `{ pet: 1 }` | Listagem de solicitações recebidas para um pet específico. |
| `interesses_adocao` | `status` | `{ status: 1 }` | Filtros rápidos do painel administrativo da ONG. |

---

## 3. Lacunas de Negócio e Funcionalidades Incompletas

Ao cruzar o código do Frontend/Mobile com as tabelas do PostgreSQL e MongoDB, identificamos as seguintes ausências críticas:

### 🔴 1. Eventos Órfãos (Gargalo de Segurança/Regra)
* **Status atual:** A coleção `eventos_ong` no MongoDB armazena apenas `id`, `nome`, `local`, `data` e `status`. Não há relacionamento com a `Organizacao`.
* **Impacto:** O endpoint `/api/ong/eventos` retorna `eventoOngRepository.findAll()`. Ou seja, **todas as ONGs enxergam e podem alterar os eventos de todas as outras ONGs**. Não há separação lógica de quem criou o evento.
* **Correção:** Adicionar `@DocumentReference` ou `organizacaoId` em `EventoOng.java` e ajustar o controller para filtrar pela organização do usuário logado.

### 🔴 2. Parâmetros de Split de Pagamento do Mercado Pago Inexistentes
* **Status atual:** O cronograma prevê repasse automático de valores ("X% pra Pet Shop A - Y% comissão pra MyBuddy").
* **Impacto:** A tabela `petshops` não possui campos para guardar credenciais de recebimento do lojista (ex: `mp_access_token` ou `mp_user_id` do petshop) e nem a taxa de comissão contratada (`comissao_porcentagem`). Sem esses dados persistidos na tabela `petshops`, o backend não consegue realizar o split de pagamento dinamicamente no gateway.
* **Correção:** Adicionar campos de comissão e integração MP na tabela `petshops`.

### 🟡 3. Persistência do Carrinho de Compras
* **Status atual:** O carrinho de compras é mantido 100% no estado das aplicações Angular e Flutter.
* **Impacto:** Se o usuário iniciar a compra no mobile e quiser finalizar no site (ou vice-versa), ou simplesmente trocar de aparelho/limpar o cache, a seleção de produtos é perdida.
* **Oportunidade:** Para uma experiência premium, criar uma tabela/coleção rápida de `carrinho` ou persistir o carrinho temporariamente no Redis utilizando o ID do usuário como chave.

---

## 4. Riscos de Configuração e Infraestrutura

### ⚠️ spring.jpa.hibernate.ddl-auto=update ativo em Produção
* **Risco:** Nos arquivos `application.properties` e `application-docker.properties`, a propriedade `ddl-auto` está configurada como `update`. Em produção, o Hibernate pode executar alterações automáticas na estrutura do banco com base nas classes Java. Isso causa travamento de tabelas (Locks), risco de perda de dados e incompatibilidade direta com as migrations controladas pelo **Flyway**.
* **Mitigação:** Alterar para `none` ou `validate` no perfil de produção/docker.

---

## 5. Propostas de Alteração (Código e SQL)

Abaixo estão os scripts sugeridos para aplicação em uma nova migration do Flyway (`V4__Otimizacoes_E_Indices_Producao.java`) e as correções nas classes Java.

### Proposta de Script Java (`V4__Otimizacoes_E_Indices_Producao.java`)
```sql
-- 1. Índices para otimização de Performance nas buscas transacionais
CREATE INDEX idx_payments_mp_payment_id ON payments (mp_payment_id) WHERE mp_payment_id IS NOT NULL;
CREATE INDEX idx_payments_usuario_id ON payments (usuario_id);
CREATE INDEX idx_payments_pedido_id ON payments (pedido_id);
CREATE INDEX idx_payments_campanha_id ON payments (campanha_id);

CREATE INDEX idx_pedidos_cliente_id ON pedidos (cliente_id);
CREATE INDEX idx_pedidos_petshop_id ON pedidos (petshop_id);
CREATE INDEX idx_pedidos_status ON pedidos (status);

CREATE INDEX idx_produtos_petshop_id ON produtos (petshop_id);
CREATE INDEX idx_produtos_subcategoria_id ON produtos (subcategoria_id);

CREATE INDEX idx_servicos_petshop_id ON servicos (petshop_id);

CREATE INDEX idx_agendamentos_cliente_id ON agendamentos (cliente_id);
CREATE INDEX idx_agendamentos_pet_id ON agendamentos (pet_id);
CREATE INDEX idx_agendamentos_datas_conflito ON agendamentos (data_hora, data_hora_fim);

CREATE INDEX idx_cupons_petshop_id ON cupons (petshop_id);

-- 2. Integridade Referencial Faltante no Postgres
ALTER TABLE cupons_usuarios 
    ADD CONSTRAINT fk_cupons_usuarios_pedido 
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL;

-- 3. Melhoria de Negócio: Campos necessários para Split do Mercado Pago na tabela de Petshops
ALTER TABLE petshops ADD COLUMN taxa_comissao DECIMAL(5, 2) DEFAULT 10.00; -- default 10% comissão
ALTER TABLE petshops ADD COLUMN mp_user_id VARCHAR(100); -- ID do vendedor do MP para split
ALTER TABLE petshops ADD COLUMN mp_merchant_account_id VARCHAR(100);
```

### Proposta de Alterações MongoDB (Java)

1. **`EventoOng.java`**: Adicionar a referência da ONG dona do evento:
```java
@Document(collection = "eventos_ong")
@Getter @Setter @NoArgsConstructor
public class EventoOng implements Identifiable {
    @Id
    private Long id;
    
    private String nome;
    private String local;
    private String data;
    private String status;

    @Indexed
    @DocumentReference(lazy = true)
    private Organizacao organizacao; // Vinculação necessária para segurança
}
```

2. **`InteresseAdocao.java`**: Incluir indexação nos relacionamentos:
```java
@Document(collection = "interesses_adocao")
public class InteresseAdocao implements Identifiable {
    // ...
    @Indexed
    @DocumentReference(lazy = true)
    private Usuario usuario;

    @Indexed
    @DocumentReference(lazy = true)
    private Pet pet;

    @Indexed
    private StatusInteresse status;
    // ...
}
```
