# La Historia de Hermes Contada en Diagramas 🗺️

Este documento explica la evolución de la arquitectura del Proyecto Hermes a través de los diagramas que creamos, siguiendo el modelo C4. Cada diagrama es como un "zoom" que nos acerca más a la solución final.

---
### 1. El Origen del Problema: `HERMES-Arquitectura Actual.drawio.svg`

**En resumen:** "Así estábamos y por esto nos dolía."

Este primer diagrama es la foto del "antes". Muestra el sistema monolítico con bases de datos locales en cada tienda que se sincronizaban cada 15 minutos. Es la evidencia visual del problema: un cliente online podía comprar un producto que se acababa de vender en una tienda física, creando una pésima experiencia. Este diagrama fue clave para justificar por qué necesitábamos un cambio radical.

![Diagrama de Arquitectura Actual](diagramas/HERMES-Arquitectura%20Actual.drawio.svg)


---
### 2. La Visión General: `HERMES-Nv1 Contexto.drawio.svg`

**En resumen:** "Este es nuestro nuevo universo y quién vive en él."

Este es el Nivel 1 (Contexto). Pusimos a nuestro nuevo sistema, "Hermes", en el centro de una caja negra. No nos importaba qué había adentro todavía. Lo importante era definir sus fronteras y cómo interactuaría con el mundo exterior: los clientes, los empleados de tienda, el e-commerce y los sistemas de punto de venta (POS). Aquí fue donde decidimos que la comunicación debía ser **en tiempo real** a través de APIs.

![Diagrama de Contexto Nv1](diagramas/HERMES-Nv1%20Contexto.drawio.svg)


---
### 3. Abriendo la Caja: `HERMES-Nv2 - Contenedores.drawio.svg`

**En resumen:** "Estas son las grandes piezas del motor que construiremos."

Este es el Nivel 2 (Contenedores). Hicimos "zoom" dentro de la caja de Hermes para definir sus componentes tecnológicos principales. Decidimos usar una arquitectura serverless en AWS con "piezas de lego" como:
- **API Gateway:** La puerta de entrada a nuestros servicios.
- **AWS Lambdas:** Pequeñas funciones que contienen la lógica de cada microservicio (`stock`, `order`, `pos`).
- **DynamoDB/PostgreSQL:** Nuestra base de datos centralizada y única fuente de la verdad.

Este diagrama es el mapa para los desarrolladores, mostrando qué grandes componentes hay que construir y cómo se hablan entre ellos.

![Diagrama de Contenedores Nv2](diagramas/HERMES-Nv2%20-%20Contenedores.drawio.svg)


---
### 4. El Plano de Construcción: `HERMES-N3 - Componentes Referencia.drawio.svg`

**En resumen:** "Así se construye cada pieza del motor por dentro."

Este es el Nivel 3 (Componentes), el "zoom" final. Tomamos una de las piezas del nivel anterior (una Lambda) y definimos su arquitectura interna. Decidimos usar un patrón de diseño en capas, como en un restaurante:
- **`Handler/Resource` (El Mesero):** Recibe las peticiones HTTP.
- **`Service` (El Chef):** Contiene la lógica de negocio pura.
- **`Repository` (El Ayudante de Cocina):** Se comunica con la base de datos.

Este diagrama sirve como una **plantilla reutilizable** para asegurar que todos los microservicios se construyan de la misma manera, manteniendo el código limpio, organizado y fácil de probar.


![Diagrama de Contenedores Nv3](diagramas/HERMES-N3-ComponentesReferencia.drawio.svg)

