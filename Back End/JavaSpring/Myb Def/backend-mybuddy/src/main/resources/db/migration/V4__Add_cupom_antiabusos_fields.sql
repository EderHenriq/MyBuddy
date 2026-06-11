-- ── Migração V4: Prevenção de Abuso de Cupons ──
-- Adiciona campos de validade temporal, valor mínimo e limites de uso à tabela cupons.
-- Cria tabela cupons_usuarios para controle de uso único por usuário (anti-fraude).

-- 1. Adicionar novos campos à tabela cupons
ALTER TABLE cupons ADD COLUMN data_inicio DATE;
ALTER TABLE cupons ADD COLUMN data_expiracao DATE;
ALTER TABLE cupons ADD COLUMN valor_minimo_pedido DECIMAL(10, 2);
ALTER TABLE cupons ADD COLUMN limite_uso_geral INTEGER;
ALTER TABLE cupons ADD COLUMN uso_atual INTEGER NOT NULL DEFAULT 0;

-- 2. Criar tabela de rastreamento de uso de cupons por usuário
CREATE TABLE cupons_usuarios (
    id          BIGSERIAL PRIMARY KEY,
    cupom_id    BIGINT NOT NULL,
    usuario_id  BIGINT NOT NULL,
    pedido_id   BIGINT,
    usado_em    TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cupons_usuarios_cupom FOREIGN KEY (cupom_id) REFERENCES cupons(id) ON DELETE CASCADE,
    CONSTRAINT uk_cupom_usuario UNIQUE (cupom_id, usuario_id)
);

-- Índice para buscas por usuário (ex: "quais cupons este usuário já usou?")
CREATE INDEX idx_cupons_usuarios_usuario_id ON cupons_usuarios (usuario_id);
-- Índice para buscas por cupom (ex: "quantos usuários usaram este cupom?")
CREATE INDEX idx_cupons_usuarios_cupom_id ON cupons_usuarios (cupom_id);
