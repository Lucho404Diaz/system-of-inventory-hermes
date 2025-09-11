package entity;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.Instant;


@Entity
@Table(name = "pos_sales")
public class PosSaleEntity extends PanacheEntityBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public String storeId;
    public String productId;
    public int quantity;
    public Instant saleTimestamp;

    public PosSaleEntity() {
    }

    public PosSaleEntity(String storeId, String productId, int quantity) {
        this.storeId = storeId;
        this.productId = productId;
        this.quantity = quantity;
        this.saleTimestamp = Instant.now();
    }
}
