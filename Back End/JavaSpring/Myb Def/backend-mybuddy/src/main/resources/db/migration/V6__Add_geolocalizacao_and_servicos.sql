-- ── 1. Geolocalização em Petshops ──
ALTER TABLE petshops ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE petshops ADD COLUMN longitude DOUBLE PRECISION;
ALTER TABLE petshops ADD COLUMN raio_entrega_km DOUBLE PRECISION;

-- ── 2. Geolocalização em Endereços de Entrega ──
ALTER TABLE enderecos_entrega ADD COLUMN latitude DOUBLE PRECISION;
ALTER TABLE enderecos_entrega ADD COLUMN longitude DOUBLE PRECISION;

-- ── 3. Criar Tabela de Serviços (Marketplace de Serviços) ──
CREATE TABLE servicos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    preco DECIMAL(10, 2) NOT NULL,
    duracao_minutos INTEGER NOT NULL,
    petshop_id BIGINT NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_servicos_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE
);

-- ── 4. Criar Tabela de Agendamentos ──
CREATE TABLE agendamentos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    pet_id BIGINT NOT NULL,
    servico_id BIGINT NOT NULL,
    data_hora TIMESTAMP NOT NULL,
    data_hora_fim TIMESTAMP NOT NULL,
    status VARCHAR(20) NOT NULL,
    profissional_nome VARCHAR(255),
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_agendamentos_servico FOREIGN KEY (servico_id) REFERENCES servicos(id) ON DELETE CASCADE
);
