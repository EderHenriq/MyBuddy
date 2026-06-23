package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Connection;
import java.sql.Statement;

public class V4__Otimizacoes_E_Indices_Producao extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        String databaseProductName = connection.getMetaData().getDatabaseProductName();
        boolean isPostgres = "PostgreSQL".equalsIgnoreCase(databaseProductName);

        try (Statement statement = connection.createStatement()) {
            // 1. Índices para otimização de Performance nas buscas transacionais
            if (isPostgres) {
                statement.execute("CREATE INDEX IF NOT EXISTS idx_payments_mp_payment_id ON payments (mp_payment_id) WHERE mp_payment_id IS NOT NULL");
            } else {
                // H2 não suporta partial indexes (WHERE clause em CREATE INDEX)
                statement.execute("CREATE INDEX IF NOT EXISTS idx_payments_mp_payment_id ON payments (mp_payment_id)");
            }

            statement.execute("CREATE INDEX IF NOT EXISTS idx_payments_usuario_id ON payments (usuario_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_payments_pedido_id ON payments (pedido_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_payments_campanha_id ON payments (campanha_id)");

            statement.execute("CREATE INDEX IF NOT EXISTS idx_pedidos_cliente_id ON pedidos (cliente_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_pedidos_petshop_id ON pedidos (petshop_id)");
            // Note: idx_pedidos_status já é criado na V3__Performance_Indexes.java

            statement.execute("CREATE INDEX IF NOT EXISTS idx_produtos_petshop_id ON produtos (petshop_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_produtos_subcategoria_id ON produtos (subcategoria_id)");

            statement.execute("CREATE INDEX IF NOT EXISTS idx_servicos_petshop_id ON servicos (petshop_id)");

            statement.execute("CREATE INDEX IF NOT EXISTS idx_agendamentos_cliente_id ON agendamentos (cliente_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_agendamentos_pet_id ON agendamentos (pet_id)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_agendamentos_datas_conflito ON agendamentos (data_hora, data_hora_fim)");

            statement.execute("CREATE INDEX IF NOT EXISTS idx_cupons_petshop_id ON cupons (petshop_id)");

            // 2. Integridade Referencial Faltante no Postgres/H2
            statement.execute("ALTER TABLE cupons_usuarios ADD CONSTRAINT fk_cupons_usuarios_pedido FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE SET NULL");

            // 3. Melhoria de Negócio: Campos necessários para Split do Mercado Pago na tabela de Petshops
            statement.execute("ALTER TABLE petshops ADD COLUMN taxa_comissao DECIMAL(5, 2) DEFAULT 10.00");
            statement.execute("ALTER TABLE petshops ADD COLUMN mp_user_id VARCHAR(100)");
            statement.execute("ALTER TABLE petshops ADD COLUMN mp_merchant_account_id VARCHAR(100)");
        }
    }
}
