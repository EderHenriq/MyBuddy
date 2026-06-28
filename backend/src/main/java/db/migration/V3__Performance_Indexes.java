package db.migration;

import org.flywaydb.core.api.migration.BaseJavaMigration;
import org.flywaydb.core.api.migration.Context;
import java.sql.Connection;
import java.sql.Statement;

public class V3__Performance_Indexes extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        Connection connection = context.getConnection();
        String databaseProductName = connection.getMetaData().getDatabaseProductName();
        
        try (Statement statement = connection.createStatement()) {
            if ("PostgreSQL".equalsIgnoreCase(databaseProductName)) {
                statement.execute("CREATE EXTENSION IF NOT EXISTS pg_trgm");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_produtos_nome_trgm ON produtos USING gin (LOWER(nome) gin_trgm_ops)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_pedidos_status ON pedidos (status)");
            } else {
                // Para bancos de testes (H2), cria índices B-Tree normais compatíveis
                statement.execute("CREATE INDEX IF NOT EXISTS idx_produtos_nome_lower ON produtos (nome)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_pedidos_status ON pedidos (status)");
            }
        }
    }
}
