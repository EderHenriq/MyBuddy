-- ── Adicionar coluna version à tabela de produtos para Optimistic Locking ──
ALTER TABLE produtos ADD COLUMN version BIGINT DEFAULT 0 NOT NULL;
