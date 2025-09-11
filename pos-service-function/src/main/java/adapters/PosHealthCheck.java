package adapters;


import io.vertx.mutiny.pgclient.PgPool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import java.util.logging.Logger;


@Liveness
@ApplicationScoped
public class PosHealthCheck implements HealthCheck {

    private final PgPool client;
    private final Logger logger = Logger.getLogger(PosHealthCheck.class.getName());

    @Inject
    public PosHealthCheck(PgPool client) {
        this.client = client;
    }

    @Override
    public HealthCheckResponse call() {
        try {
            logger.info("Verificando conexión a la base de datos de stock...");
            client.query("SELECT 1")
                    .execute()
                    .await().indefinitely();
            return HealthCheckResponse.up("Conexión a la base de datos de stock es funcional");
        } catch (Exception e) {
            return HealthCheckResponse.down("Conexión a la base de datos de stock falló");
        }
    }
}