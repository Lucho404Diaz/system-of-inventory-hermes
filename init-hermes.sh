#!/bin/bash

# ==================================================
# Script Interactivo de Inicio para el Entorno de Hermes
# ==================================================

# --- Colores para los mensajes ---
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
NC='\033[0m' # Sin Color

kill_services() {
    echo -e "\n${YELLOW}Buscando servicios de Hermes en los puertos 8081, 8082, 8083...${NC}"

    # Array de puertos a verificar
    PORTS=(8081 8082 8083)

    for port in "${PORTS[@]}"; do
        # lsof -ti :$port busca el PID del proceso en el puerto especificado
        PID=$(lsof -ti :$port)

        # Si se encontró un PID, se termina el proceso
        if [ -n "$PID" ]; then
            echo -e "Proceso encontrado en el puerto ${port} (PID: ${PID}). ${RED}Terminando...${NC}"
            kill -9 $PID
        else
            echo -e "Puerto ${port} está libre."
        fi
    done

    echo -e "\n${GREEN}✅ Limpieza completada.${NC}"
}


# --- Función para mostrar el menú y lanzar el servicio ---
start_service_menu() {
    echo -e "\n${YELLOW}¿Qué deseas iniciar en modo de desarrollo?${NC}"

    options=("stock-service (Puerto 8081)" "order-service (Puerto 8082)" "pos-service (Puerto 8083)" "Iniciar TODO el sistema Hermes" "Solo Detener Servicios" "Salir")

    select opt in "${options[@]}"
    do
        case $opt in
            "stock-service (Puerto 8081)")
                echo -e "\n${BLUE}Iniciando 'stock-service-function' en http://localhost:8081 ...${NC}"
                ./gradlew :stock-service-function:quarkusDev
                break
                ;;
            "order-service (Puerto 8082)")
                echo -e "\n${BLUE}Iniciando 'order-service-function' en http://localhost:8082 ...${NC}"
                ./gradlew :order-service-function:quarkusDev
                break
                ;;
            "pos-service (Puerto 8083)")
                echo -e "\n${BLUE}Iniciando 'pos-service-function' en http://localhost:8083 ...${NC}"
                ./gradlew :pos-service-function:quarkusDev
                break
                ;;
            "Iniciar TODO el sistema Hermes")
                echo -e "\n${CYAN}Iniciando todos los servicios de Hermes en segundo plano...${NC}"
                echo -e "  - Stock Service:   http://localhost:8081"
                echo -e "  - Order Service:   http://localhost:8082"
                echo -e "  - POS Service:     http://localhost:8083"

                ./gradlew :stock-service-function:quarkusDev &
                ./gradlew :order-service-function:quarkusDev &
                ./gradlew :pos-service-function:quarkusDev &

                echo -e "\n${GREEN}✅ Todos los servicios se están iniciando. Revisa la salida para ver su progreso.${NC}"
                echo -e "${YELLOW}Para detenerlos, deberás cerrar esta terminal o usar el comando 'killall java'.${NC}"
                wait
                break
                ;;
            "Solo Detener Servicios")
                kill_services
                break
                ;;
            "Salir")
                echo -e "\n${GREEN}Saliendo. ¡Hasta luego!${NC}"
                break
                ;;
            *) echo -e "Opción inválida.";;
        esac
    done
}

# ==================================================
# INICIO DEL SCRIPT
# ==================================================

# --- Mensaje de Bienvenida ---
echo -e "${GREEN}=====================================${NC}"
echo -e "${GREEN}🚀   Bienvenido al Proyecto Hermes   🚀${NC}"
echo -e "${GREEN}=====================================${NC}"
echo ""

# --- Levantar Contenedores de Docker ---
echo -e "${BLUE}Levantando componentes dependientes (Base de Datos H2)...${NC}"
echo ""
(cd .docker && docker-compose up -d)

# Verifica si Docker Compose tuvo éxito
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ ¡Entorno listo! La base de datos corriendo: Postgrest 15.${NC}"
    # --- LÍNEA NUEVA Y CLAVE ---
    echo -e "   ${CYAN}-> Puedes acceder con los siguientes datos de conexión url: jdbc:postgresql://localhost:5432/hermesdb
           POSTGRES_DB: hermesdb
           POSTGRES_USER: hermesuser
           POSTGRES_PASSWORD: hermespassword
     ${NC}"
    # Si todo salió bien, muestra el menú
    start_service_menu
else
    echo ""
    echo -e "\033[0;31m❌ Error al levantar los contenedores de Docker. Verifica que Docker esté corriendo.${NC}"
fi

echo ""