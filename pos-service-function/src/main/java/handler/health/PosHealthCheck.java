package handler.health;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

import javax.sql.DataSource;
import java.sql.Connection;


@Liveness
@ApplicationScoped
public class PosHealthCheck implements HealthCheck {

    @Inject
    DataSource dataSource;

    @Override
    public HealthCheckResponse call() {
        try (Connection connection = dataSource.getConnection()) {
            boolean isValid = connection.isValid(1);

            String checkName = "Conexión a Base de Datos PostgreSQL (JDBC)";

            if (isValid) {
                return HealthCheckResponse.named(checkName).up().build();
            } else {
                return HealthCheckResponse.named(checkName)
                        .down()
                        .withData("reason", "La conexión no es válida.")
                        .build();
            }
        } catch (Exception e) {
            return HealthCheckResponse.named("Conexión a Base de Datos PostgreSQL (JDBC)")
                    .down()
                    .withData("error", e.getMessage())
                    .build();
        }
    }
}