# Arquitectura del Proyecto Hermes

## Estructura del Proyecto (Monorepo)

Este proyecto utiliza un monorepo gestionado con Gradle para contener todos los microservicios y configuraciones en un solo lugar.

```
hermes-monorepo/
├── .docker/                # Configuración de Docker Compose y scripts de BD.
│   ├── postgres/
│   │   ├── data/           # (Ignorado por Git) Datos persistentes de la BD.
│   │   └── init.sql        # Script de inicialización del esquema de la BD.
│   └── docker-compose.yml  # Orquesta los contenedores de infraestructura.
│
├── .otel/                  # Contiene el agente de OpenTelemetry.
│   └── opentelemetry-javaagent.jar
│
├── common-libs/            # Módulo para código compartido (DTOs, entidades, etc.).
├── order-service-function/ # Microservicio para gestionar órdenes.
├── pos-service-function/   # Microservicio para ingestar ventas de POS.
├── stock-service-function/ # Microservicio central para la lógica de stock.
│
├── gradlew                 # Wrapper de Gradle.
├── settings.gradle         # Define los módulos del monorepo.
├── build.gradle            # Configuración de build para el proyecto raíz.
├── init-hermes.sh          # Script de gestión del entorno.
└── README.md
```

## Componentes del Sistema

### Infraestructura (Docker)
- **PostgreSQL (`hermes-postgres-database`):** Base de datos relacional que actúa como la única fuente de la verdad para el estado del inventario, órdenes y ventas.
- **Jaeger (`hermes-jaeger`):** Sistema de trazado distribuido. Recibe la telemetría de los servicios (vía OpenTelemetry) y provee una UI para visualizar las trazas en `http://localhost:16686`.

### Microservicios
#### 1. Stock Service
- **Responsabilidad:** Es el corazón del sistema. Gestiona la lógica de consultar, reservar y confirmar cambios en el inventario. Es la única autoridad sobre el estado del stock.
- **Puerto:** `8081`

#### 2. Order Service
- **Responsabilidad:** Maneja el flujo de creación de órdenes del e-commerce. Se comunica con el `stock-service` para reservar el inventario antes de confirmar una orden.
- **Puerto:** `8082`

#### 3. POS Service
- **Responsabilidad:** Simula la ingesta de ventas desde un Punto de Venta (POS) físico. Se comunica con el `stock-service` para confirmar y descontar el inventario de forma inmediata.
- **Puerto:** `8083`

## Guía de Pruebas de API con `curl`

Asegúrate de tener el entorno corriendo con `./init-hermes.sh` antes de ejecutar estas pruebas.

### Stock Service (Puerto 8081)

#### Verifica la salud del servicio
```bash
curl http://localhost:8081/stock/v1/health/live
```

#### Consulta el stock de un producto
```bash
curl http://localhost:8081/stock/v1/items/PRODUCTO-A
```

### Order Service (Puerto 8082)

#### Crea una nueva orden (y reserva el stock)
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"productId": "PRODUCTO-B", "quantity": 1}' \
  http://localhost:8082/orders/v1/items
```
*Después de ejecutar esto, si consultas el stock de `PRODUCTO-B` en el `stock-service`, verás que la cantidad reservada ha aumentado.*

#### Prueba la comunicación entre servicios (Endpoint de Saludo)
```bash
curl http://localhost:8082/orders/v1/items/saludo
```

### POS Service (Puerto 8083)

#### Registra una venta de tienda física (y confirma el stock)
```bash
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"storeId": "TIENDA-01", "productId": "PRODUCTO-A", "quantity": 5}' \
  http://localhost:8083/pos/v1/sales
```
*Después de ejecutar esto, si consultas el stock de `PRODUCTO-A`, verás que la cantidad física (`quantity_on_hand`) ha disminuido.*