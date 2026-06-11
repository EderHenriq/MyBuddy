-- ── Migração V5: Fila de Aprovação para Petshops ──
-- Adiciona o campo status_aprovacao à tabela petshops.
-- O campo é NOT NULL com valor padrão PENDENTE_APROVACAO para todos os registros existentes,
-- garantindo que os Petshops já cadastrados precisem passar pela aprovação.
--
-- Nota: Organizacao (ONG) é persistida no MongoDB e não é gerenciada pelo Flyway.
-- O campo statusAprovacao no MongoDB é adicionado automaticamente pelo Spring Data.

ALTER TABLE petshops
    ADD COLUMN status_aprovacao VARCHAR(30) NOT NULL DEFAULT 'PENDENTE_APROVACAO';

-- Cria índice para facilitar buscas por status (ex: admin listando todos os pendentes)
CREATE INDEX idx_petshops_status_aprovacao ON petshops (status_aprovacao);
