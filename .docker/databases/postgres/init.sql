-- =================================================================
-- MODELO DE DATOS CON LÓGICA DE RESERVA DE STOCK
-- =================================================================

-- Extensión para generar UUIDs (para la tabla de órdenes)
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- -----------------------------------------------------------------
-- 1. TABLA DE STOCK (CON RESERVAS)
-- -----------------------------------------------------------------
CREATE TABLE stock_items (
                             id BIGSERIAL PRIMARY KEY,
                             product_id VARCHAR(255) UNIQUE NOT NULL,
    -- Cantidad física total en el inventario
                             quantity_on_hand INT NOT NULL,
    -- Cantidad actualmente reservada en procesos de pago
                             quantity_reserved INT NOT NULL DEFAULT 0,
                             updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),

    -- Restricción para asegurar que nunca reservemos más de lo que tenemos
                             CONSTRAINT check_reserved_quantity CHECK (quantity_reserved >= 0 AND quantity_reserved <= quantity_on_hand)
);

CREATE UNIQUE INDEX idx_stock_items_product_id ON stock_items(product_id);

-- Datos de prueba para el inventario
INSERT INTO stock_items (product_id, quantity_on_hand) VALUES ('PRODUCTO-A', 100);
INSERT INTO stock_items (product_id, quantity_on_hand) VALUES ('PRODUCTO-B', 50);

INSERT INTO stock_items (product_id, quantity_on_hand, quantity_reserved) VALUES ('PRODUCTO-C', 5, 2);


-- -----------------------------------------------------------------
-- 2. TABLA PARA EL SERVICIO DE ÓRDENES (order-service)
-- -----------------------------------------------------------------
CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        order_id UUID DEFAULT uuid_generate_v4(),
                        product_id VARCHAR(255) NOT NULL,
                        quantity INT NOT NULL,
                        status VARCHAR(50) NOT NULL, -- Ej: PENDING_PAYMENT, PAID, SHIPPED, CANCELED
                        created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_orders_product_id ON orders(product_id);

-- Datos de prueba para las órdenes
INSERT INTO orders (product_id, quantity, status) VALUES ('PRODUCTO-A', 2, 'PAID');
INSERT INTO orders (product_id, quantity, status) VALUES ('PRODUCTO-C', 1, 'SHIPPED');
INSERT INTO orders (product_id, quantity, status) VALUES ('PRODUCTO-C', 2, 'PENDING_PAYMENT');


-- -----------------------------------------------------------------
-- 3. TABLA PARA EL SERVICIO DE INGESTA POS (pos-service)
-- -----------------------------------------------------------------
CREATE TABLE pos_sales (
                           id BIGSERIAL PRIMARY KEY,
                           store_id VARCHAR(100) NOT NULL,
                           product_id VARCHAR(255) NOT NULL,
                           quantity INT NOT NULL,
                           sale_timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_pos_sales_store_product ON pos_sales(store_id, product_id);

-- Datos de prueba para las ventas de POS
INSERT INTO pos_sales (store_id, product_id, quantity) VALUES ('TIENDA-01', 'PRODUCTO-B', 1);
INSERT INTO pos_sales (store_id, product_id, quantity) VALUES ('TIENDA-02', 'PRODUCTO-A', 5);

\echo '>>> Base de datos de Hermes (con reservas) creada y poblada exitosamente <<<'