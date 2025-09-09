package services;

import entity.StockItemEntity;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;
import repository.StockItemRepository;



@ApplicationScoped
public class StockService {

    private final StockItemRepository stockItemRepository;
    private static final String PRODUCT_QUERY = "productId";

    @Inject
    public StockService(StockItemRepository stockItemRepository) {
        this.stockItemRepository = stockItemRepository;
    }


    @WithSession
    public Uni<Integer> getAvailableStock(String productId) {
        return stockItemRepository.find(PRODUCT_QUERY, productId)
                .firstResult()
                .onItem().ifNotNull().transform(stockItem -> stockItem.quantityOnHand - stockItem.quantityReserved)
                .onItem().ifNull().failWith(NotFoundException::new);
    }


    public Uni<StockItemEntity> reserveStock(String productId, int quantity) {
        return stockItemRepository.find(PRODUCT_QUERY, productId)
                .firstResult()
                .onItem().ifNotNull().call(stockItem -> {
                    // Verificación de stock antes de la reserva
                    if (stockItem.quantityOnHand - stockItem.quantityReserved < quantity) {
                        return Uni.createFrom().failure(new RuntimeException("No hay stock suficiente para la reserva."));
                    }
                    stockItem.quantityReserved += quantity;
                    // Persistir el cambio de manera reactiva dentro de una transacción.
                    return stockItemRepository.persist(stockItem);
                });

    }

    public Uni<StockItemEntity> confirmSale(String productId, int quantity) {
        return stockItemRepository.find(PRODUCT_QUERY, productId)
                .firstResult()
                .onItem().ifNotNull().call(stockItem -> {
                    // Validar si la cantidad reservada es suficiente.
                    if (stockItem.quantityReserved < quantity) {
                        return Uni.createFrom().failure(new RuntimeException("La cantidad a confirmar excede la cantidad reservada."));
                    }
                    stockItem.quantityOnHand -= quantity;
                    stockItem.quantityReserved -= quantity;
                    // Persistir el cambio de manera reactiva dentro de una transacción.
                    return stockItemRepository.persist(stockItem);
                });
    }


    public Uni<StockItemEntity> releaseReservation(String productId, int quantity) {
        return stockItemRepository.find(PRODUCT_QUERY, productId)
                .firstResult()
                .onItem().ifNotNull().call(stockItem -> {
                    // Validar que no se libere más stock del que se reservó.
                    if (stockItem.quantityReserved < quantity) {
                        return Uni.createFrom().failure(new RuntimeException("La cantidad a liberar excede la cantidad reservada."));
                    }
                    stockItem.quantityReserved -= quantity;
                    // Persistir el cambio de manera reactiva dentro de una transacción.
                    return stockItemRepository.persist(stockItem);
                });
    }

}
