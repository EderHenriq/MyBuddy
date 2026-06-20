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
ALTER TABLE petshops ADD COLUMN taxa_comissao DECIMAL(5, 2) DEFAULT 10.00;
ALTER TABLE petshops ADD COLUMN mp_user_id VARCHAR(100);
ALTER TABLE petshops ADD COLUMN mp_merchant_account_id VARCHAR(100);
