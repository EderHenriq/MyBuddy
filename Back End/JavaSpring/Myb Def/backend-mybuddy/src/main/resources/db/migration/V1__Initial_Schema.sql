-- ── 1. Criar Tabela de Petshops ──
CREATE TABLE petshops (
    id BIGSERIAL PRIMARY KEY,
    nome_fantasia VARCHAR(255) NOT NULL,
    email_contato VARCHAR(255),
    cnpj VARCHAR(255) NOT NULL UNIQUE,
    telefone_contato VARCHAR(255),
    endereco VARCHAR(255),
    descricao TEXT,
    website VARCHAR(255)
);

-- ── 2. Criar Tabela de Categorias ──
CREATE TABLE categorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL UNIQUE
);

-- ── 3. Criar Tabela de SubCategorias ──
CREATE TABLE subcategorias (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    categoria_id BIGINT NOT NULL,
    CONSTRAINT fk_subcategorias_categoria FOREIGN KEY (categoria_id) REFERENCES categorias(id) ON DELETE CASCADE
);

-- ── 4. Criar Tabela de Produtos ──
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
    CONSTRAINT fk_produtos_subcategoria FOREIGN KEY (subcategoria_id) REFERENCES subcategorias(id),
    CONSTRAINT fk_produtos_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE
);

-- ── 5. Criar Tabela de Fotos do Produto ──
CREATE TABLE fotos_produto (
    id BIGSERIAL PRIMARY KEY,
    url VARCHAR(500) NOT NULL,
    produto_id BIGINT NOT NULL,
    CONSTRAINT fk_fotos_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);

-- ── 6. Criar Tabela de Endereços de Entrega ──
CREATE TABLE enderecos_entrega (
    id BIGSERIAL PRIMARY KEY,
    cep VARCHAR(20) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    complemento VARCHAR(255),
    bairro VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(50) NOT NULL
);

-- ── 7. Criar Tabela de Pedidos ──
CREATE TABLE pedidos (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    petshop_id BIGINT NOT NULL,
    endereco_entrega_id BIGINT,
    valor_total DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    data_criacao TIMESTAMP,
    data_atualizacao TIMESTAMP,
    CONSTRAINT fk_pedidos_petshop FOREIGN KEY (petshop_id) REFERENCES petshops(id) ON DELETE CASCADE,
    CONSTRAINT fk_pedidos_endereco_entrega FOREIGN KEY (endereco_entrega_id) REFERENCES enderecos_entrega(id) ON DELETE SET NULL
);

-- ── 8. Criar Tabela de Itens do Pedido ──
CREATE TABLE itens_pedido (
    id BIGSERIAL PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    produto_id BIGINT NOT NULL,
    quantidade INTEGER NOT NULL,
    preco_unitario DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_itens_pedido_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    CONSTRAINT fk_itens_pedido_produto FOREIGN KEY (produto_id) REFERENCES produtos(id)
);

-- ── 9. Criar Tabela de Avaliações do Produto ──
CREATE TABLE avaliacoes_produto (
    id BIGSERIAL PRIMARY KEY,
    produto_id BIGINT NOT NULL,
    cliente_id BIGINT NOT NULL,
    nota INTEGER NOT NULL,
    comentario VARCHAR(1000),
    data_criacao TIMESTAMP NOT NULL,
    CONSTRAINT fk_avaliacoes_produto_produto FOREIGN KEY (produto_id) REFERENCES produtos(id) ON DELETE CASCADE
);

-- ── 10. Criar Tabela de Assinaturas de Doação ──
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

-- ── 11. Criar Tabela de Pagamentos ──
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
