-- ── 1. Criar Tabela de Cupons ──
CREATE TABLE cupons (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(100) UNIQUE NOT NULL,
    percentual_desconto DECIMAL(5, 2) NOT NULL,
    petshop_id BIGINT,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    CONSTRAINT fk_cupons_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE
);

-- ── 2. Adicionar valor_minimo_frete_gratis a Petshops ──
ALTER TABLE petshops ADD COLUMN valor_minimo_frete_gratis DECIMAL(10, 2);

-- ── 3. Adicionar campos de frete e cupom a Pedidos ──
ALTER TABLE pedidos ADD COLUMN valor_frete DECIMAL(10, 2);
ALTER TABLE pedidos ADD COLUMN cupom_desconto VARCHAR(100);
ALTER TABLE pedidos ADD COLUMN valor_desconto DECIMAL(10, 2);
