package adapters;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import io.vertx.mutiny.pgclient.PgPool;


@Liveness
@ApplicationScoped
public class StockHealthCheck implements HealthCheck {

    private final PgPool client;

    @Inject
    public StockHealthCheck(PgPool client) {
        this.client = client;
    }

    @Override
    public HealthCheckResponse call() {
        // Ejecuta una consulta simple para verificar la conectividad de la base de datos.
        // Se usa el método await() para bloquear y esperar el resultado.
        // En un contexto de API, es mejor usar la cadena de Mutiny.
        try {
            client.query("SELECT 1")
                    .execute()
                    .await().indefinitely();
            return HealthCheckResponse.up("Conexión a la base de datos de stock es funcional");
        } catch (Exception e) {
            return HealthCheckResponse.down("Conexión a la base de datos de stock falló");
        }
    }
}