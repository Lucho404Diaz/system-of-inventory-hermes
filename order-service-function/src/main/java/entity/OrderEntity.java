package entity;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class OrderEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    @Column(name = "order_id", unique = true, updatable = false, nullable = false)
    public UUID orderId;

    @Column(name = "product_id", nullable = false)
    public String productId;

    @Column(name = "quantity", nullable = false)
    public int quantity;

    @Column(name = "status", nullable = false)
    public String status; // Ej: PENDING_PAYMENT, PAID, CANCELED

    @Column(name = "created_at")
    public ZonedDateTime createdAt;


    public OrderEntity() {
        this.orderId = UUID.randomUUID();
        this.createdAt = ZonedDateTime.now();
    }

    public OrderEntity(String productId, int quantity, String status) {
        this();
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
    }
}
