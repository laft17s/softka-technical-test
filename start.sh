#!/bin/bash

# Script de inicio rápido para Laft Banking
# Autor: Luis Arcángel Farro Terán
# Fecha: 2025-12-06

set -e

echo "🚀 Laft Banking - Inicio Rápido"
echo "================================"
echo ""

# Colores
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Verificar que estamos en el directorio correcto
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}❌ Error: No se encuentra docker-compose.yml${NC}"
    echo "Por favor ejecuta este script desde el directorio raíz del proyecto"
    exit 1
fi

# Función para verificar si Docker está corriendo
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        echo -e "${RED}❌ Error: Docker no está corriendo${NC}"
        echo "Por favor inicia Docker Desktop y vuelve a intentar"
        exit 1
    fi
    echo -e "${GREEN}✅ Docker está corriendo${NC}"
}

# Función para levantar servicios
start_services() {
    echo ""
    echo -e "${YELLOW}🐳 Levantando servicios con Docker Compose...${NC}"
    echo "   Esto puede tardar 3-5 minutos la primera vez..."
    docker-compose up -d --build
    echo -e "${GREEN}✅ Servicios iniciados${NC}"
}

# Función para esperar a que los servicios estén listos
wait_for_services() {
    echo ""
    echo -e "${YELLOW}⏳ Esperando a que los servicios estén listos...${NC}"
    
    # Esperar PostgreSQL
    echo -n "   PostgreSQL... "
    for i in {1..30}; do
        if docker exec laft-postgres pg_isready -U postgres > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC}"
            break
        fi
        sleep 2
    done
    
    # Esperar Client Service
    echo -n "   Client Service... "
    for i in {1..60}; do
        if curl -s http://localhost:8081/api/clients > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC}"
            break
        fi
        sleep 2
    done
    
    # Esperar Account Service
    echo -n "   Account Service... "
    for i in {1..60}; do
        if curl -s http://localhost:8082/api/accounts > /dev/null 2>&1; then
            echo -e "${GREEN}✓${NC}"
            break
        fi
        sleep 2
    done
    
    echo -e "${GREEN}✅ Todos los servicios están listos${NC}"
}

# Función para mostrar información de acceso
show_info() {
    echo ""
    echo "================================"
    echo -e "${GREEN}🎉 ¡Todo listo!${NC}"
    echo "================================"
    echo ""
    echo "📡 Servicios disponibles:"
    echo ""
    echo "  🔹 Client Service:    http://localhost:8081/api"
    echo "  🔹 Account Service:   http://localhost:8082/api"
    echo "  🔹 Composite Service: http://localhost:8083/graphiql"
    echo "  🔹 pgAdmin:           http://localhost:5050"
    echo "  🔹 Kafka UI:          http://localhost:8090"
    echo ""
    echo "🗄️  Base de Datos:"
    echo "  Host: localhost:5432"
    echo "  Database: laft_bank"
    echo "  User: postgres"
    echo "  Password: postgres"
    echo ""
    echo "📝 Próximos pasos:"
    echo "  1. Importa la colección: insomnia/Laft-Banking-API.json"
    echo "  2. Prueba los endpoints en Postman/Insomnia"
    echo "  3. Ver logs: docker-compose logs -f"
    echo ""
    echo "🛑 Para detener: docker-compose down"
    echo ""
}

# Función para verificar servicios
verify_services() {
    echo ""
    echo -e "${YELLOW}🔍 Verificando servicios...${NC}"
    
    # Verificar Client Service
    if curl -s http://localhost:8081/api/clients > /dev/null 2>&1; then
        echo -e "   Client Service:  ${GREEN}✓ OK${NC}"
    else
        echo -e "   Client Service:  ${YELLOW}⏳ Iniciando...${NC}"
    fi
    
    # Verificar Account Service
    if curl -s http://localhost:8082/api/accounts > /dev/null 2>&1; then
        echo -e "   Account Service: ${GREEN}✓ OK${NC}"
    else
        echo -e "   Account Service: ${YELLOW}⏳ Iniciando...${NC}"
    fi
    
    # Verificar PostgreSQL
    if docker exec laft-postgres psql -U postgres -d laft_bank -c "SELECT 1;" > /dev/null 2>&1; then
        echo -e "   PostgreSQL:      ${GREEN}✓ OK${NC}"
    else
        echo -e "   PostgreSQL:      ${RED}✗ ERROR${NC}"
    fi
    
    # Verificar Composite Service
    if curl -s http://localhost:8083/graphiql > /dev/null 2>&1; then
        echo -e "   Composite Service: ${GREEN}✓ OK${NC}"
    else
        echo -e "   Composite Service: ${YELLOW}⏳ Iniciando...${NC}"
    fi
}

# Main
main() {
    check_docker
    start_services
    wait_for_services
    verify_services
    show_info
}

# Ejecutar
main
