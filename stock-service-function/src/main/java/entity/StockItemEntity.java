package entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "stock_items")
public class StockItemEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "product_id", unique = true, nullable = false)
    public String productId;

    @Column(name = "quantity_on_hand", nullable = false)
    public int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    public int quantityReserved;

    @Column(name = "updated_at")
    public ZonedDateTime updatedAt;

    public StockItemEntity() {
    }

    public StockItemEntity(String productId, int quantityOnHand) {
        this.productId = productId;
        this.quantityOnHand = quantityOnHand;
        this.quantityReserved = 0;
        this.updatedAt = ZonedDateTime.now();
    }
}
