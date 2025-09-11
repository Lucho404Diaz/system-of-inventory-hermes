# Proyecto Hermes - Sistema de Gestión de Inventario en Tiempo Real

## Descripción Funcional

Hermes es un sistema de gestión de inventario diseñado para una cadena de tiendas minoristas que soluciona los problemas de latencia e inconsistencia de un sistema tradicional. El sistema anterior sincronizaba el inventario de cada tienda con una base de datos central cada 15 minutos, lo que provocaba discrepancias entre el stock online y el físico, resultando en una mala experiencia para el cliente y pérdida de ventas.

Este proyecto implementa una **arquitectura de microservicios distribuida, reactiva y observable** que centraliza el estado del inventario en tiempo real, asegurando que tanto las ventas en puntos físicos (POS) como las del e-commerce operen sobre una única fuente de la verdad.

## Requisitos Previos

Para levantar y probar el entorno de desarrollo de Hermes, necesitarás tener instalado el siguiente software:

- **Git:** para clonar el repositorio.
- **JDK 21+:** para compilar y ejecutar los servicios Java.
- **Docker y Docker Compose:** para levantar la infraestructura de base de datos y monitoreo.
- **Un cliente de API:** como Postman o `curl` para probar los endpoints.
- **Terminal compatible con Unix:** (Bash/Zsh) para ejecutar el script de inicio.

## Guía de Levantamiento

El proyecto está diseñado para ser levantado con un único script que gestiona todo el entorno.

### 1. Clonar el Repositorio
```bash
git clone <URL_DEL_REPOSITORIO>
cd system-of-inventory-hermes
```

### 2. Descargar el Agente de OpenTelemetry
Este proyecto utiliza un agente de OpenTelemetry para la observabilidad "no-code".

- Ve a la página de releases: **[OpenTelemetry Java Agent Releases](https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/latest)**
- Descarga el archivo `opentelemetry-javaagent.jar`.
- Crea una carpeta `.otel` en la raíz del proyecto y guarda el archivo allí (`.otel/opentelemetry-javaagent.jar`).

### 3. Dar Permisos al Script de Inicio
La primera vez, necesitas hacer que el script de gestión sea ejecutable:
```bash
chmod +x init-hermes.sh
```

### 4. ¡Iniciar Hermes!
Ejecuta el script para levantar todo el entorno:
```bash
./init-hermes.sh
```
El script realizará las siguientes acciones:
1.  Verificará que Docker y el agente de OTEL estén disponibles.
2.  Levantará los contenedores de PostgreSQL y Jaeger.
3.  Te presentará un menú interactivo para iniciar uno o todos los microservicios.

## Arquitectura y Guía de Pruebas

Para una descripción detallada de la arquitectura, la función de cada servicio y una guía completa con comandos `curl` para probar cada endpoint, por favor consulta nuestro documento de arquitectura:

➡️ **[DOCUMENTO DE ARQUITECTURA](ARCHITECTURE.md)**