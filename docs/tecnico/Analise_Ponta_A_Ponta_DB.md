# Relatório de Análise de Ponta a Ponta: Banco de Dados MyBuddy (PostgreSQL & MongoDB)

Realizamos uma auditoria completa na estrutura, mapeamento, índices e integridade dos bancos de dados do MyBuddy. Abaixo detalhamos a avaliação do estado atual e as oportunidades de melhoria identificadas.

---

## 1. Arquitetura Híbrida (Veredito Geral)
A separação de responsabilidades está **100% correta e bem estruturada**:
* **PostgreSQL:** Usado para entidades relacionais e transacionais (`Produto`, `Pedido`, `Cupom`, `Servico`, `Agendamento`, `Payment`). Garante conformidade ACID, controle rígido de estoque, consistência de preços e histórico de transações financeiras.
* **MongoDB:** Usado para entidades documentais e de conteúdo dinâmico (`Usuario`, `Pet`, `Organizacao`, `InteresseAdocao`, `Chat`). Oferece flexibilidade para dados de perfil, múltiplos contatos, upload de fotos estruturadas de pets e histórico de conversação sem o peso de JOINS complexos.

---

## 2. PostgreSQL: Diagnóstico e Oportunidades

### ⚠️ Tabelas Órfãs/Legadas na Base de Desenvolvimento
* **Identificação:** Durante a auditoria física das tabelas no banco de dados (`mybuddy-postgres`), identificamos a presença das tabelas `pets`, `organizacoes`, `users`, `roles`, `interesses_adocao` e `user_roles` com dados de maio de 2026.
* **Causa:** No início do projeto, essas entidades eram JPA/PostgreSQL. Ao serem migradas para MongoDB, as tabelas físicas persistiram no volume Docker do Postgres porque a propriedade `spring.jpa.hibernate.ddl-auto` está configurada como `update` (que apenas cria/altera colunas, nunca remove tabelas antigas).
* **Impacto:** Poluição visual do esquema, consumo desnecessário de armazenamento e risco de desenvolvedores executarem queries contra as tabelas erradas.
* **Recomendação:** Executar o script de limpeza abaixo no Postgres de desenvolvimento para remover as tabelas legadas:
  ```sql
  DROP TABLE IF EXISTS user_roles CASCADE;
  DROP TABLE IF EXISTS roles CASCADE;
  DROP TABLE IF EXISTS interesses_adocao CASCADE;
  DROP TABLE IF EXISTS pets CASCADE;
  DROP TABLE IF EXISTS organizacoes CASCADE;
  DROP TABLE IF EXISTS users CASCADE;
  DROP TABLE IF EXISTS fotos_pet CASCADE;
  DROP TABLE IF EXISTS chats CASCADE;
  DROP TABLE IF EXISTS eventos_ong CASCADE;
  ```

---

## 3. MongoDB: Diagnóstico e Oportunidades

### 🔴 Ausência de Índices e Risco de Integridade
* **Identificação:** Ao inspecionar os índices nas coleções do MongoDB (`pets`, `usuarios`, `organizacoes`), constatamos que **apenas o índice padrão `_id` está criado**.
* **Impacto em Performance:** 
  * Qualquer consulta filtrando pets por status (`statusAdocao`) ou organização (`organizacao`) na tela de listagem resulta em um **Collection Scan (COLLSCAN)**, forçando o MongoDB a ler todos os documentos da coleção.
  * Sincronizações de usuários via Keycloak que barrem por email (`email`) ou ID do Keycloak (`keycloakId`) farão buscas sequenciais lentas.
* **Impacto em Integridade:** Sem um índice único (`unique: true`), o MongoDB não garante fisicamente que dois usuários não se cadastrem com o mesmo e-mail caso haja alguma falha lógica na camada de serviço.
* **Recomendação:**
  1. Habilitar a auto-criação de índices no MongoDB adicionando no `application.properties`:
     ```properties
     spring.data.mongodb.auto-index-creation=true
     ```
  2. Adicionar índices nas classes de modelo do Spring Data:
     * **`Usuario.java`:**
       ```java
       @Indexed(unique = true)
       private String email;

       @Indexed(unique = true)
       private String keycloakId;
       ```
     * **`Pet.java`:**
       ```java
       @Indexed
       private StatusAdocao statusAdocao;

       @Indexed
       private Long adotanteId;
       ```

---

## 4. Otimizações de Infraestrutura e Cache (Validadas)
* **HikariCP:** A pool configurada com máximo de 20 conexões e timeouts explícitos evita exaustão de conexões em cenários de picos de carga.
* **Redis Caching:** O cache transparente de catálogo de produtos está operando corretamente e protegendo o PostgreSQL de queries repetitivas.
* **Índices de Busca:** O índice trigram GIN em `produtos.nome` resolveu a lentidão em buscas parciais (`LIKE '%...%'`), e o índice B-Tree em `pedidos.status` otimizou os relatórios de vendas.
