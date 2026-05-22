# Hotel Booking System

A microservices-based hotel booking platform built with **Spring Boot**, **Apache Kafka**, and **PostgreSQL**. Demonstrates the *database-per-service* pattern, event-driven communication, and containerized local deployment with Docker Compose.

> Built as a personal project to practice distributed systems patterns (microservices, async messaging, service-owned data stores) on a realistic domain.

---

## Architecture

The system is split into three independent Spring Boot services, each owning its own PostgreSQL instance:

| Service           | Responsibility                                         | Database       |
|-------------------|--------------------------------------------------------|----------------|
| `client-service`  | Customer accounts and profiles                         | `client_db`    |
| `room-service`    | Hotel rooms, availability, pricing                     | `room_db`      |
| `booking-service` | Reservations; orchestrates the booking flow            | `booking_db`   |

```
        ┌─────────────────┐
        │     Client      │
        └────────┬────────┘
                 │ REST
       ┌─────────┼──────────┐
       │         │          │
       ▼         ▼          ▼
 ┌──────────┐ ┌────────┐ ┌──────────┐
 │  Client  │ │  Room  │ │ Booking  │
 │ Service  │ │ Service│ │ Service  │
 └────┬─────┘ └───┬────┘ └─────┬────┘
      │           │            │
      ▼           ▼            ▼
   client_db   room_db    booking_db
      │           │            │
      └───────────┼────────────┘
                  │
                  ▼
             ┌─────────┐
             │  Kafka  │   ← async events between services
             └─────────┘
```

### Communication

- **Synchronous (REST):** used between services when an immediate response is required (e.g. `booking-service` querying `room-service` for room availability before creating a reservation).
- **Asynchronous (Kafka):** used for cross-service events that don't need to block the user — booking confirmations, availability updates, cancellations. This keeps the booking flow responsive even if downstream consumers are slow.

### Why database-per-service?

Each service owns its data. No service queries another service's database directly — all cross-service data flows through REST or Kafka. This trades off some short-term complexity (no joins across services) for long-term loose coupling: each service can evolve its schema independently and scale separately.

---

## Tech Stack

- **Java 21**, **Spring Boot 3.x**, **Spring Data JPA**
- **Apache Kafka** (Confluent images) + **Zookeeper**
- **PostgreSQL** — 3 isolated instances, one per service
- **Docker Compose** for local orchestration
- **Maven** (multi-module project)

---

## Running locally

### Prerequisites
- Docker & Docker Compose
- JDK 21+
- Maven 3.8+

### 1. Start infrastructure (Kafka + PostgreSQL instances)

```bash
docker-compose up -d
```

This brings up Zookeeper, Kafka, and three PostgreSQL instances on different ports.

### 2. Build all services

```bash
mvn clean package
```

### 3. Run each service in a separate terminal

```bash
cd client-service && mvn spring-boot:run
cd room-service && mvn spring-boot:run
cd booking-service && mvn spring-boot:run
```

### Ports

| Component         | Port   |
|-------------------|--------|
| Kafka             | `9092` |
| Zookeeper         | `2181` |
| `booking-db`      | `5433` |
| `room-db`         | `5434` |
| `client-db`       | `5435` |
| `client-service`  | `8083` |
| `room-service`    | `8082` |
| `booking-service` | `8081` |

---

## API Overview

### Booking Service — `/api/v1/bookings`

| Method   | Endpoint                          | Description                                                     |
|----------|-----------------------------------|-----------------------------------------------------------------|
| `POST`   | `/api/v1/bookings`                | Create a new booking                                            |
| `GET`    | `/api/v1/bookings/find`           | Find available rooms for a date range (`enteringDate`, `leavingDate`) |
| `GET`    | `/api/v1/bookings/{id}`           | Get booking details by id                                       |
| `PUT`    | `/api/v1/bookings/{id}`           | Update an existing booking                                      |
| `DELETE` | `/api/v1/bookings/{id}`           | Cancel a booking                                                |
| `GET`    | `/api/v1/bookings/client/{clientId}` | List all bookings for a given client                         |

### Client Service



### Room Service — `/api/rooms`

| Method  | Endpoint                  | Description                                                          |
|---------|---------------------------|----------------------------------------------------------------------|
| `POST`  | `/api/rooms`              | Create a new room                                                    |
| `GET`   | `/api/rooms`              | List all rooms, or filter by name via `?name=` query param           |
| `PUT`   | `/api/rooms/{id}/price`   | Update the price of a room (`?price=` query param)                   |

#### Example: list rooms by name
```bash
curl "http://localhost:8082/api/rooms?name=Deluxe"
```

#### Example: update room price
```bash
curl -X PUT "http://localhost:8082/api/rooms/42/price?price=149.99"
```

### Example: finding available rooms

```bash
curl "http://localhost:8083/api/v1/bookings/find?enteringDate=2026-06-01&leavingDate=2026-06-05"
```

### Example: creating a booking

```bash
curl -X POST http://localhost:8083/api/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "roomId": 42,
    "enteringDate": "2026-06-01",
    "leavingDate": "2026-06-05"
  }'
```


### Example: cancelling a booking

```bash
curl -X DELETE http://localhost:8083/api/v1/bookings/123
```

---
## Kafka topics

| Topic         | Producer       | Consumer          | Purpose                                                                 |
|---------------|----------------|-------------------|-------------------------------------------------------------------------|
| `room-prices` | `room-service` | `booking-service` | Replicate room pricing to a local read model inside `booking-service` so the booking flow doesn't have to call `room-service` on every read |

### Why this topic exists

The `booking-service` needs room prices to calculate booking totals, but reaching out to `room-service` over REST on every request adds latency and creates tight coupling. Instead:

1. When a room's price changes in `room-service`, it publishes a `RoomPriceEvent` to the `room-prices` topic.
2. `booking-service` consumes the event and updates its own local `RoomInfo` table.
3. Read-side queries in `booking-service` hit the local copy — fast, no cross-service call needed.

This is a small-scale application of the **read model replication** pattern (sometimes called CQRS-lite): each service keeps a denormalised, eventually-consistent view of the data it needs from other services.

### Future events to add

The current implementation only synchronises room prices. Natural next events:
- `booking-created` (booking → room) to mark a room as occupied
- `booking-cancelled` (booking → room) to release a room
- `client-registered` (client → booking) to cache basic client info locally

---

## Design decisions

- **Microservices over monolith** — chosen as a learning goal to practice distributed systems patterns. For a real product at this scale a modular monolith would actually be a more pragmatic starting point.
- **Database-per-service over shared DB** — enforces service boundaries and prevents the "distributed monolith" anti-pattern where services share a schema and can't evolve independently.
- **Kafka over synchronous REST for events** — decouples the booking flow from room state updates; a successful booking doesn't have to wait for every downstream consumer.
- **Docker Compose for local dev** — single command to bring up the whole infrastructure; production would use Kubernetes or managed services.

---

## Known limitations & future improvements

This is a learning project, and several things are deliberately simplified:

- **No API Gateway yet** — clients currently hit each service directly. Would add Spring Cloud Gateway as a single entry point.
- **No service discovery** — services know each other's addresses statically. Eureka or Consul would solve this.
- **No distributed tracing** — debugging a request across services requires reading three sets of logs. Zipkin or Jaeger would help.
- **No Saga pattern for distributed transactions** — currently relies on eventual consistency without compensating actions for failures mid-flow.
- **No Outbox pattern** — there's a small window where a database write succeeds but the Kafka publish fails, leading to inconsistent state. The transactional outbox pattern would close this gap.
- **Naive Kafka error handling** — the `room-prices` consumer swallows parse errors with a broad `catch (Exception e)`, which means a malformed message is logged and silently dropped while the offset still advances. A real implementation needs a dead-letter topic and explicit retry policy.
- **No authentication/authorization** — endpoints are open. OAuth2 (e.g. Keycloak) would be the natural next step.
- **No integration tests across services** — only unit tests inside each service.

---

## Project structure

```
hotel-booking-system/
├── docker-compose.yml      # Kafka + 3 PostgreSQL instances
├── pom.xml                 # parent POM (multi-module)
├── client-service/         # customer profiles
├── room-service/           # rooms & availability
└── booking-service/        # reservations & orchestration
```
