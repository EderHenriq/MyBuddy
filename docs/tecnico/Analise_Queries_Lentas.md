# Análise de Queries Lentas com EXPLAIN ANALYZE (MY-193)

Esta análise identifica os gargalos de desempenho nas buscas frequentes da base de dados PostgreSQL do MyBuddy e detalha os planos de execução obtidos.

---

## 1. Busca de Produtos por Nome (Wildcard Parcial)

### Query Analisada
```sql
EXPLAIN ANALYZE SELECT * FROM produtos WHERE LOWER(nome) LIKE '%teste%';
```

### Diagnóstico Inicial (Sem Índices)
```text
                                              QUERY PLAN                                              
------------------------------------------------------------------------------------------------------
 Seq Scan on produtos  (cost=0.00..10.30 rows=1 width=3056) (actual time=2.586..2.587 rows=0 loops=1)
   Filter: (lower((nome)::text) ~~ '%teste%'::text)
   Rows Removed by Filter: 2
 Planning Time: 4.554 ms
 Execution Time: 2.933 ms
```

### Gargalo Identificado
Ocorre um **Seq Scan** (varredura sequencial) em toda a tabela `produtos`. Embora o tempo de execução seja baixo com poucos registros, a complexidade é $O(N)$. Em produção, com milhares de itens catalogados, essa busca causará alto consumo de CPU e latência.

### Solução Proposta
A busca utiliza correspondência parcial de string (`LIKE '%...%'`). Um índice B-Tree convencional não auxilia neste tipo de busca. Utilizaremos o módulo `pg_trgm` do PostgreSQL para criar um índice **GIN (Generalized Inverted Index)** trigram sobre `LOWER(nome)`.

---

## 2. Consulta de Pedidos por Status

### Query Analisada
```sql
EXPLAIN ANALYZE SELECT * FROM pedidos WHERE status = 'PENDENTE';
```

### Diagnóstico Inicial (Sem Índices)
```text
                                             QUERY PLAN                                             
----------------------------------------------------------------------------------------------------
 Seq Scan on pedidos  (cost=0.00..12.38 rows=1 width=388) (actual time=0.017..0.017 rows=0 loops=1)
   Filter: ((status)::text = 'PENDENTE'::text)
 Planning Time: 3.448 ms
 Execution Time: 0.170 ms
```

### Gargalo Identificado
Também ocorre **Seq Scan** em `pedidos`. Como a busca por status é constante no painel de administração e petshops, a ausência de índice causará gargalo à medida que o volume de pedidos crescer.

### Solução Proposta
Criar um índice **B-Tree** padrão na coluna `status` da tabela `pedidos`, já que as buscas filtram por valores exatos da enumeração de status.
