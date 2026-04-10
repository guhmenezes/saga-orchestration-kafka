# 🧩 Saga Orchestration with Kafka (Java + Spring Boot)

This project demonstrates a **Saga Orchestration pattern** implementation using **Apache Kafka** and **Spring Boot**, simulating a distributed transaction across multiple microservices.

---

## 🚀 Overview

The system is composed of independent microservices coordinated by an **Orchestrator Service**, ensuring consistency across services using an **event-driven architecture**.

### 🧠 Flow

1. A request is sent to the **Orchestrator**
2. The Orchestrator starts the saga by calling **Order Service**
3. Order Service emits `OrderCreatedEvent`
4. Orchestrator listens and triggers **Inventory Service**
5. Inventory reserves stock and emits `StockReservedEvent`
6. Orchestrator triggers **Payment Service**
7. Payment processes and emits `PaymentApprovedEvent`
8. Orchestrator finalizes the saga ✅

---

## 🏗️ Architecture

```
Client → Orchestrator → Order → Inventory → Payment
           ↑              ↓         ↓          ↓
           └──────── Kafka Events (async) ─────┘
```

---

## 📦 Microservices

* **orchestrator-service** → Coordinates the saga flow
* **order-service** → Handles order creation
* **inventory-service** → Manages stock reservation
* **payment-service** → Processes payments
* **common-lib** → Shared event contracts (Java records)

---

## 🧪 Technologies

* Java 17+
* Spring Boot
* Spring Kafka
* Apache Kafka
* PostgreSQL
* Maven
* Docker (optional)

---

## ⚙️ Environment Configuration

Create a `.env` file based on the example:

```bash
cp .env.example .env
```

### Example:

```
DB_URL=jdbc:postgresql://localhost:5432/saga_db
DB_USER=postgres
DB_PASSWORD=postgres

KAFKA_BOOTSTRAP_SERVERS=localhost:9092
```

---

## ▶️ Running the Project

### 1. Start Kafka (Docker recommended)

```bash
docker-compose up -d
```

---

### 2. Build shared library

```bash
cd common-lib
mvn clean install
```

---

### 3. Start microservices

Run each service:

```bash
mvn spring-boot:run
```

Services:

* Orchestrator → `localhost:8080`
* Order → `localhost:8081`
* Inventory → `localhost:8082`
* Payment → `localhost:8083`

---

## 📡 Start the Saga

```bash
POST http://localhost:8080/saga/start
```

### Example Body

```json
{
  "productId": "123",
  "quantity": 1
}
```

---

## 📊 Events

* `CreateOrderEvent`
* `OrderCreatedEvent`
* `ReserveStockEvent`
* `StockReservedEvent`
* `PaymentRequestEvent`
* `PaymentApprovedEvent`

---

## 🔐 Best Practices Applied

* ✅ Event-driven architecture
* ✅ Saga Orchestration pattern
* ✅ Shared event contracts via library
* ✅ Environment variables for sensitive data
* ✅ Loose coupling between services

---

## 📌 Future Improvements

* Add MongoDB for saga state tracking
* Implement retry and compensation mechanisms
* Add observability (logs/tracing)
* Dockerize all services
* Deploy to cloud environment

---

## 👨‍💻 Author

Developed by **Gustavo Menezes**

---

## ⭐ Final Thoughts

This project was built to demonstrate real-world backend architecture patterns used in distributed systems.

If you found it useful, feel free to ⭐ the repository!
