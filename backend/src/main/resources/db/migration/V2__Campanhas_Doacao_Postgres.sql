CREATE TABLE campanhas_doacao (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(150) NOT NULL,
    descricao TEXT,
    meta DECIMAL(10, 2) NOT NULL,
    arrecadado DECIMAL(10, 2) NOT NULL DEFAULT 0,
    pet_id BIGINT,
    organizacao_id BIGINT NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    data_expiracao TIMESTAMP,
    status VARCHAR(30) NOT NULL,
    data_criacao TIMESTAMP NOT NULL,
    data_atualizacao TIMESTAMP
);

CREATE INDEX idx_campanhas_doacao_organizacao_id ON campanhas_doacao (organizacao_id);
CREATE INDEX idx_campanhas_doacao_status ON campanhas_doacao (status);
CREATE INDEX idx_campanhas_doacao_categoria_status ON campanhas_doacao (categoria, status);

ALTER TABLE payments
    ADD CONSTRAINT fk_payments_campanha
    FOREIGN KEY (campanha_id) REFERENCES campanhas_doacao(id)
    ON DELETE SET NULL;
