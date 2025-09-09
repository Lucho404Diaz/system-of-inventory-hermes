package repository;

import entity.StockItemEntity;
import io.quarkus.hibernate.reactive.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StockItemRepository implements PanacheRepositoryBase<StockItemEntity, Long> {
}
