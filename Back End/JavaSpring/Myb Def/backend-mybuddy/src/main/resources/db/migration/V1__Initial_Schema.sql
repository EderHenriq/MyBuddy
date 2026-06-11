CREATE TABLE petshops (
    id BIGSERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(255) NOT NULL,
    email_contato VARCHAR(255),
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    telefone_contato VARCHAR(255),
    endereco VARCHAR(255),
    descricao TEXT,
    website VARCHAR(255),
    valor_minimo_frete_gratis DECIMAL(10, 2),
    status_aprovacao VARCHAR(30) NOT NULL DEFAULT 'PENDENTE_APROVACAO',
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    raio_entrega_km DOUBLE PRECISION
);

CREATE INDEX idx_petshops_status_aprovacao ON petshops (status_aprovacao);

CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE subcategorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_subcategorias_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

CREATE TABLE produtos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    subcategoria_id BIGINT NOT NULL,
    preco DECIMAL(10, 2) NOT NULL,
    estoque INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL,
    petshop_id BIGINT NOT NULL,
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP,
    version BIGINT DEFAULT 0 NOT NULL,
    CONSTRAINT fk_produtos_subcategoria FOREIGN KEY (subcategoria_id) REFERENCES subcategorias(id),
    CONSTRAINT fk_produtos_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE
);

CREATE TABLE fotos_produto (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    produto_id BIGINT NOT NULL,
    CONSTRAINT fk_fotos_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);

CREATE TABLE enderecos_entrega (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION
);

CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    petshop_id BIGINT NOT NULL,
    endereco_entrega_id BIGINT,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP,
    valor_frete DECIMAL(10, 2),
    cupom_desconto VARCHAR(100),
    valor_desconto DECIMAL(10, 2),
    CONSTRAINT fk_pedidos_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE,
    CONSTRAINT fk_pedidos_endereco_entrega FOREIGN KEY (endereco_entrega_id) REFERENCES enderecos_entrega(id) ON DELETE SET NULL
);

CREATE TABLE itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_itens_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_pedido_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

CREATE TABLE avaliacoes_produto (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    nota INTEGER NOT NULL,
    comentario VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_avaliacoes_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);

CREATE TABLE donation_subscriptions (
    id BIGSERIAL PRIMARY KEY,
    mp_preapproval_id VARCHAR(255) UNIQUE,
    usuario_id BIGINT NOT NULL,
    organizacao_id BIGINT,
    amount DECIMAL(10, 2),
    frequency VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE payments (
    id BIGSERIAL PRIMARY KEY,
    mp_preference_id VARCHAR(255) UNIQUE,
    mp_payment_id VARCHAR(255),
    usuario_id BIGINT NOT NULL,
    pet_id BIGINT,
    campanha_id BIGINT,
    organizacao_id BIGINT,
    pedido_id BIGINT,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT fk_payments_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL
);

CREATE TABLE cupons (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(100) UNIQUE NOT NULL,
    percentual_desconto DECIMAL(5, 2) NOT NULL,
    petshop_id BIGINT,
    ativo BOOLEAN DEFAULT TRUE NOT NULL,
    data_inicio DATE,
    data_expiracao DATE,
    valor_minimo_pedido DECIMAL(10, 2),
    limite_uso_geral INTEGER,
    uso_atual INTEGER NOT NULL DEFAULT 0,
    CONSTRAINT fk_cupons_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE
);

CREATE TABLE cupons_usuarios (
    id          BIGSERIAL PRIMARY KEY,
    cupom_id    BIGINT NOT NULL,
    usuario_id  BIGINT NOT NULL,
    pedido_id   BIGINT,
    usado_em    TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_cupons_usuarios_cupom FOREIGN KEY (cupom_id) REFERENCES cupons(id) ON DELETE CASCADE,
    CONSTRAINT uk_cupom_usuario UNIQUE (cupom_id, usuario_id)
);

CREATE INDEX idx_cupons_usuarios_usuario_id ON cupons_usuarios (usuario_id);
CREATE INDEX idx_cupons_usuarios_cupom_id ON cupons_usuarios (cupom_id);

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
