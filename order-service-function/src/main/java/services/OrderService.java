package services;


import dtos.StandardResponse;
import dtos.StockRequest;
import entity.OrderEntity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import restclients.StockServiceClient;

@ApplicationScoped
public class OrderService {
    private final StockServiceClient stockServiceClient;

    @Inject
    public OrderService(@RestClient StockServiceClient stockServiceClient) {
        this.stockServiceClient = stockServiceClient;
    }

    public Uni<StandardResponse> saludoStock() {
        return stockServiceClient.getSaludo();
    }

    @Transactional
    public Uni<OrderEntity> createOrder(StockRequest request) {
        return stockServiceClient.reserveStock(request.productId, request)
                .onItem().transformToUni(stockResponse -> {
                    if (stockResponse.success()) {
                        OrderEntity newOrder = new OrderEntity(request.productId, request.quantity, "PENDING_PAYMENT");
                        return newOrder.persistAndFlush().onItem().transform(v -> newOrder);
                    } else {
                        return Uni.createFrom().failure(
                                new WebApplicationException(stockResponse.message(), Response.Status.CONFLICT) // 409 Conflict es más apropiado
                        );
                    }
                })
                .onFailure().transform(e -> {
                    if (e instanceof WebApplicationException) {
                        return e;
                    }
                    return new WebApplicationException("Error de comunicación con el servicio de stock.", e, Response.Status.SERVICE_UNAVAILABLE);
                });
    }
}
