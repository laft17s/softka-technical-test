# Laft Technical Test - Microservices Architecture

Sistema bancario basado en microservicios con Spring Boot 3.2.1, Kafka y PostgreSQL.

## 📋 Tabla de Contenidos

- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Patrones de Diseño](#patrones-de-diseño)
- [Inicio Rápido](#inicio-rápido)
- [API Endpoints](#api-endpoints)
- [Funcionalidades](#funcionalidades)
- [Testing](#testing)

---

## 🏗️ Arquitectura

```
laft-technical-test/
├── common-lib/              # DTOs, constantes compartidas
├── shared-repositories/     # Repositorios JPA compartidos
├── client-service/          # Microservicio: Cliente/Persona (Puerto 8081)
├── account-service/         # Microservicio: Cuenta/Movimientos (Puerto 8082)
├── bank-mgnt-composite/     # Microservicio: Composite GraphQL (Puerto 8083)
├── docker/                  # Configuraciones Docker
├── BaseDatos.sql           # Script de base de datos
└── docker-compose.yml      # Orquestación de servicios
```

### Microservicios

#### 1. Client Service (Puerto 8081)
- Gestión de clientes y personas
- Publicador de eventos Kafka
- Endpoints: `/api/clients`

#### 2. Account Service (Puerto 8082)
- Gestión de cuentas y movimientos
- Consumidor de eventos Kafka
- Validación de saldo
- Generación de reportes
- Endpoints: `/api/accounts`, `/api/transactions`, `/api/reports`

#### 3. Bank Management Composite (Puerto 8083)
- Agregador de información (Composite Pattern)
- API GraphQL
- Consulta unificada de Personas, Cuentas y Movimientos
- Endpoint: `/graphiql` (UI), `/graphql` (API)

---

## 💻 Tecnologías

- **Spring Boot**: 3.2.1
- **Java**: 17
- **Gradle**: 8.x
- **Base de datos**: PostgreSQL 16
- **Message Broker**: Apache Kafka 3.x
- **Contenedores**: Docker & Docker Compose

---

## 🎨 Patrones de Diseño

### Builder Pattern
Construcción fluida de entidades y DTOs:
```java
Client client = Client.builder()
    .name("Jose Lema")
    .age(35)
    .build();
```

### Strategy Pattern
Procesamiento de movimientos con diferentes estrategias:
- `DepositStrategy` - Procesa depósitos
- `WithdrawalStrategy` - Procesa retiros con validación de saldo

### Factory Pattern
- `ClientFactory` - Creación de clientes desde DTOs
- `TransactionStrategyFactory` - Selección de estrategia apropiada

### Constants Pattern
- Tablas de catálogo en BD (gender, account_type, status)
- Clases de constantes (ApiConstants, ErrorConstants, etc.)

### Repository Pattern
Abstracción de acceso a datos con Spring Data JPA

---

## 🚀 Inicio Rápido

### Prerrequisitos

- Docker y Docker Compose
- Java 17 (para desarrollo local)
- Gradle 8.x (incluido en wrapper)

### 1. Levantar Infraestructura

```bash
# Iniciar PostgreSQL y Kafka
docker-compose up -d postgres kafka zookeeper

# Verificar que los servicios estén corriendo
docker-compose ps
```

### 2. Inicializar Base de Datos

```bash
# Conectar a PostgreSQL
docker exec -it laft-postgres psql -U postgres

# El script BaseDatos.sql se ejecuta automáticamente
# Verificar datos
\c laft_bank
SELECT * FROM client;
```

### 3. Compilar Proyecto

```bash
# Compilar todos los módulos
./gradlew build

# O compilar módulos específicos
./gradlew :client-service:build
./gradlew :account-service:build
```

### 4. Ejecutar Microservicios

#### Opción A: Localmente

```bash
# Terminal 1: Client Service
./gradlew :client-service:bootRun

# Terminal 2: Account Service
./gradlew :account-service:bootRun
```

#### Opción B: Con Docker

```bash
# Construir y levantar todos los servicios
docker-compose up --build

# Ver logs
docker-compose logs -f client-service
docker-compose logs -f account-service
```

### 5. Verificar Servicios

```bash
# Client Service
curl http://localhost:8081/api/clients

# Account Service
curl http://localhost:8082/api/accounts

# Kafka UI (opcional)
open http://localhost:8090

# Composite Service (GraphiQL)
open http://localhost:8083/graphiql
```

---

## 📡 API Endpoints

### Client Service (Puerto 8081)

#### Clients

```bash
# Listar todos los clientes
GET /api/clients

# Obtener cliente por ID
GET /api/clients/{clientId}

# Obtener cliente por identificación
GET /api/clients/identificacion/{identification}

# Crear cliente
POST /api/clients
{
  "name": "Jose Lema",
  "gender": "M",
  "age": 35,
  "identification": "1234567890",
  "address": "Otavalo sn y principal",
  "phone": "098254785",
  "password": "1234",
  "status": "ACTIVO"
}

# Actualizar cliente
PUT /api/clients/{clientId}

# Actualización parcial
PATCH /api/clients/{clientId}

# Eliminar cliente (soft delete)
DELETE /api/clients/{clientId}
```

### Account Service (Puerto 8082)

#### Accounts

```bash
# Listar todas las cuentas
GET /api/accounts

# Obtener cuenta por número
GET /api/accounts/{accountNumber}

# Obtener cuentas por cliente
GET /api/accounts/client/{clientId}

# Crear cuenta
POST /api/accounts
{
  "accountNumber": "478758",
  "accountType": "AHORRO",
  "initialBalance": 2000.00,
  "status": "ACTIVO",
  "clientId": "550e8400-e29b-41d4-a716-446655440001"
}

# Actualizar cuenta
PUT /api/accounts/{accountNumber}

# Eliminar cuenta
DELETE /api/accounts/{accountNumber}
```

#### Transactions

```bash
# Listar todos los movimientos
GET /api/transactions

# Obtener movimiento por ID
GET /api/transactions/{id}

# Obtener movimientos por cuenta
GET /api/transactions/account/{accountNumber}

# Crear movimiento (depósito)
POST /api/transactions
{
  "accountNumber": "478758",
  "transactionType": "DEPOSITO",
  "amount": 600.00
}

# Crear movimiento (retiro)
POST /api/transactions
{
  "accountNumber": "478758",
  "transactionType": "RETIRO",
  "amount": 575.00
}

# Eliminar movimiento
DELETE /api/transactions/{id}
```

#### Reports

```bash
# Generar reporte de estado de cuenta
GET /api/reports?fecha=2022-02-08,2022-02-10&cliente={clientId}

# Ejemplo
GET /api/reports?fecha=2022-02-08,2022-02-10&cliente=550e8400-e29b-41d4-a716-446655440002
```

### Bank Management Composite (Puerto 8083)

#### GraphQL

**Endpoint**: `/graphql`

```graphql
# Consultar Persona con sus Cuentas y Movimientos
query {
  person(identification: "1234567890") {
    name
    identification
    client {
      clientId
      status
      accounts {
        accountNumber
        balance
        transactions {
          date
          amount
          transactionType
        }
      }
    }
  }
    }
  }
}

# Crear Cliente con Cuenta y Transacción Inicial
mutation {
  createClientWithAccount(
    client: {
      name: "Nuevo Cliente"
      gender: "M"
      age: 30
      identification: "9988776655"
      address: "Calle Falsa 123"
      phone: "0991122334"
      password: "1234"
      status: "ACTIVO"
    }
    account: {
      accountNumber: "556677"
      accountType: "AHORRO"
      initialBalance: 100.00
      status: "ACTIVO"
    }
  ) {
    name
    identification
    client {
      clientId
      status
      accounts {
        accountNumber
        initialBalance
        transactions {
          transactionType
          amount
          balance
        }
      }
    }
  }
}
```

---

## ✅ Funcionalidades

### F1: CRUD Completo
✅ Clientes, Cuentas y Movimientos con todos los verbos HTTP

### F2: Registro de Movimientos
✅ Valores positivos (depósitos) y negativos (retiros)  
✅ Actualización automática de saldo  
✅ Registro de transacciones

### F3: Validación de Saldo
✅ Mensaje "Saldo no disponible" cuando no hay fondos suficientes  
✅ Excepción personalizada `SaldoNoDisponibleException`

### F4: Reportes
✅ Estado de cuenta por cliente y rango de fechas  
✅ Cuentas con saldos  
✅ Detalle de movimientos  
✅ Formato JSON

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Todos los tests
./gradlew test

# Tests de un módulo específico
./gradlew :client-service:test
./gradlew :account-service:test

# Con reporte de cobertura
./gradlew jacocoTestReport
```

### Tests Implementados

- ✅ Pruebas unitarias de entidades
- ✅ Pruebas de servicios con Mockito
- ✅ Pruebas de integración con TestContainers

---

## 🗄️ Base de Datos

### Tablas Principales
- `person` - Información base de personas
- `client` - Clientes (hereda de person)
- `account` - Cuentas bancarias
- `transaction` - Transacciones

### Tablas de Catálogo
- `gender` - Géneros (M, F, O)
- `account_type` - Tipos de cuenta (AHORRO, CORRIENTE)
- `status` - Estados (ACTIVO, INACTIVO)

### Acceso a Base de Datos

```bash
# PostgreSQL directo
docker exec -it laft-postgres psql -U postgres -d laft_bank

# pgAdmin (interfaz web)
open http://localhost:5050
# Email: admin@laft.com
# Password: admin
# Nota: El servidor "Laft Bank Local" está pre-configurado.
# Si conectas manualmente: Host = postgres, User/Pass = postgres
```

---

## 📊 Monitoreo

### Kafka UI
```bash
open http://localhost:8090
```

Ver topics, mensajes y consumidores en tiempo real.

---

## 🛑 Detener Servicios

```bash
# Detener todos los servicios
docker-compose down

# Detener y eliminar volúmenes
docker-compose down -v
```

---

## 📝 Casos de Uso del PDF

Los datos de prueba de los casos de uso 1-5 del PDF están precargados en `BaseDatos.sql`:

1. ✅ Creación de Usuarios (Jose Lema, Marianela Montalvo, Juan Osorio)
2. ✅ Creación de Cuentas de Usuario
3. ✅ Nueva Cuenta Corriente para Jose Lema
4. ⏳ Movimientos (ejecutar vía API)
5. ⏳ Listado de Movimientos (ejecutar vía API)

---

## 🤝 Contribución

Este proyecto fue desarrollado como prueba técnica para Laft Technologies.

**Autor**: [Tu Nombre]  
**Fecha**: Diciembre 2024  
**Nivel**: Senior

