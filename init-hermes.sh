#!/bin/bash

# ======================================================
# Script de Inicio y Gestión para el Entorno de Hermes
# con Chequeos de Prerrequisitos y Monitoreo Integrado
# ======================================================

# --- Colores ---
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

# --- Variables Globales ---
OTEL_AGENT_PATH="./.otel/opentelemetry-javaagent.jar"

# --- Funciones Auxiliares ---

# Valida que Docker y el Agente de OTEL existan
check_prerequisites() {
    echo -e "${BLUE}Verificando prerrequisitos...${NC}"
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}Error: Docker no parece estar corriendo. Por favor, inicia Docker Desktop y vuelve a intentarlo.${NC}"
        exit 1
    fi
    echo -e "  - Docker: ${GREEN}OK${NC}"
    if [ ! -f "$OTEL_AGENT_PATH" ]; then
        echo -e "  - Agente OpenTelemetry: ${YELLOW}ADVERTENCIA - No encontrado. El monitoreo estará desactivado.${NC}"
    else
        echo -e "  - Agente OpenTelemetry: ${GREEN}OK${NC}"
        OTEL_AGENT_EXISTS=true
    fi
}

# Busca y detiene procesos en los puertos de los servicios
kill_services() {
    echo -e "\n${YELLOW}Buscando y deteniendo servicios de Hermes en los puertos 8081, 8082, 8083...${NC}"
    PORTS=(8081 8082 8083)
    for port in "${PORTS[@]}"; do
        PID=$(lsof -ti :$port)
        if [ -n "$PID" ]; then
            echo -e "Proceso encontrado en el puerto ${port} (PID: ${PID}). ${RED}Terminando...${NC}"
            kill -9 $PID
        else
            echo -e "Puerto ${port} está libre."
        fi
    done
    echo -e "\n${GREEN}✅ Limpieza completada.${NC}"
}

# Lanza un servicio individual con o sin monitoreo
launch_service() {
    local service_name=$1
    local service_path=$2
    local service_port=$3
    echo -e "\n${BLUE}Iniciando '${service_name}' en http://localhost:${service_port} ...${NC}"
    if [ "$OTEL_AGENT_EXISTS" = true ]; then
        echo -e "-> ${CYAN}Activando monitoreo...${NC}"
        OTEL_SERVICE_NAME=${service_name} \
        JAVA_TOOL_OPTIONS="-javaagent:$(pwd)/.otel/opentelemetry-javaagent.jar -Djava.net.preferIPv4Stack=true" \
        ./gradlew :${service_path}:quarkusDev --no-daemon
    else
        ./gradlew :${service_path}:quarkusDev --no-daemon
    fi
}

# --- Menú de Lanzamiento de Servicios ---
start_service_menu() {
    echo -e "\n${YELLOW}¿Qué servicio deseas iniciar en modo de desarrollo?${NC}"
    options=("stock-service (Puerto 8081)" "order-service (Puerto 8082)" "pos-service (Puerto 8083)" "Iniciar TODO el sistema Hermes" "Salir")
    select opt in "${options[@]}"; do
        case $opt in
            "stock-service (Puerto 8081)")
                launch_service "stock-service" "stock-service-function" "8081"; break ;;
            "order-service (Puerto 8082)")
                launch_service "order-service" "order-service-function" "8082"; break ;;
            "pos-service (Puerto 8083)")
                launch_service "pos-service" "pos-service-function" "8083"; break ;;
            "Iniciar TODO el sistema Hermes")
                echo -e "\n${CYAN}Iniciando todos los servicios de Hermes en segundo plano...${NC}"
                if [ "$OTEL_AGENT_EXISTS" = true ]; then
                    echo -e "-> ${GREEN}Activando monitoreo...${NC}"
                    TOOL_OPTIONS="-javaagent:$(pwd)/.otel/opentelemetry-javaagent.jar -Djava.net.preferIPv4Stack=true"
                    OTEL_SERVICE_NAME=stock-service JAVA_TOOL_OPTIONS="$TOOL_OPTIONS" ./gradlew :stock-service-function:quarkusDev --no-daemon &
                    OTEL_SERVICE_NAME=order-service JAVA_TOOL_OPTIONS="$TOOL_OPTIONS" ./gradlew :order-service-function:quarkusDev --no-daemon &
                    OTEL_SERVICE_NAME=pos-service JAVA_TOOL_OPTIONS="$TOOL_OPTIONS" ./gradlew :pos-service-function:quarkusDev --no-daemon &
                else
                    echo -e "-> ${YELLOW}Iniciando SIN monitoreo (agente no encontrado).${NC}"
                    ./gradlew :stock-service-function:quarkusDev --no-daemon &
                    ./gradlew :order-service-function:quarkusDev --no-daemon &
                    ./gradlew :pos-service-function:quarkusDev --no-daemon &
                fi
                echo -e "\n${GREEN}✅ Todos los servicios han sido lanzados.${NC}"
                echo -e "${YELLOW}Observa la salida de la terminal. El sistema estará completamente listo cuando veas los mensajes 'Listening on...' para los puertos 8081, 8082 y 8083.${NC}"
                echo -e "${YELLOW}Para detenerlos, cierra esta terminal o usa la opción 'Solo Detener Servicios'.${NC}"
                wait
                break ;;
            "Salir")
                break ;;
            *) echo "Opción inválida.";;
        esac
    done
}
# ==================================================
# INICIO DEL SCRIPT
# ==================================================
echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}🚀   Bienvenido al Proyecto Hermes   🚀${NC}"
echo -e "${GREEN}=====================================${NC}"
echo ""
check_prerequisites
echo -e "\n${YELLOW}¿Qué acción deseas realizar?${NC}"
tasks=("Iniciar Entorno" "Solo Detener Servicios" "Salir")
select task in "${tasks[@]}"; do
    case $task in
        "Iniciar Entorno")
            echo -e "\n${BLUE}Levantando componentes de Docker (PostgreSQL y Jaeger)...${NC}"
            (cd .docker && docker-compose up -d)
            if [ $? -eq 0 ]; then
                echo -e "${GREEN}✅ ¡Entorno Docker listo!${NC}"
                echo -e "   ${CYAN}-> Jaeger UI (Trazas): http://localhost:16686${NC}"
                echo -e "   ${CYAN}-> Base de Datos PostgreSQL: jdbc:postgresql://localhost:5432/hermesdb
           POSTGRES_USER: hermesuser
            POSTGRES_PASSWORD: hermespassword
                           ${NC}"
                start_service_menu
            else
                echo -e "\n${RED}❌ Error al levantar los contenedores de Docker.${NC}"
            fi
            break ;;
        "Solo Detener Servicios")
            kill_services
            break ;;
        "Salir")
            break ;;
        *) echo "Opción inválida.";;
    esac
done
echo -e "\n${GREEN}Proceso terminado. ¡Hasta luego!${NC}"