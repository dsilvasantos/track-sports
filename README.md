# 📊 Live Sports Tracker

A **Spring Boot application** for tracking live sports events using scheduled polling, Kafka for event streaming, and external APIs via Feign.

---

## 🚀 Features

- Polls live event data from an external API using **Feign clients**
- Publishes event updates to **Kafka topics** with automatic retry logic
- Maintains a live map of active events using a concurrent in-memory store
- Exposes REST endpoints to update event statuses (`LIVE`, `FINISHED`, etc.)
- Built-in error handling and fallback mechanisms
- Integration-ready with monitoring via Spring Boot Actuator

---

## 🛠️ Tech Stack

- **Java 21**
- **Spring Boot 3.2.x**
- **Apache Kafka**
- **Spring Kafka**
- **Spring Cloud OpenFeign**
- **Scheduled Tasks**
- **Spring Web / REST**
- **JUnit 5 + Mockito**

---

## 📂 Project Structure

```
src/
├── main/
│   ├── java/com/example/livesportstracker/
│   │   ├── controller/       # REST controllers
│   │   ├── service/          # Core services (event polling, retry, Kafka publishing)
│   │   ├── interfaces/client # Feign interfaces for external APIs
│   │   ├── model/            # Request/response and enums
│   ├── resources/
│       ├── application.yml   # Configurations (Kafka, polling, etc.)
```

---

## ⚙️ Configuration

Edit `application.yml`:

```yaml
tracker:
  topic: live-events
  polling-interval: 5 # seconds
  external-url: http://external-api.com/events

spring:
  kafka:
    bootstrap-servers: kafka:29092
```

---

## 🧪 Running Tests

Run unit and integration tests using:

```bash
mvn test
```

Tests include:
- Unit tests for core services and utils
- Integration test simulating the full event flow (REST → Feign → Kafka)

---

## 🧭 API Endpoint

**POST** `/events/status`

```json
{
  "eventId": "match-123",
  "status": "LIVE"
}
```

Marks an event as `LIVE`, enabling it for scheduled polling.

---

## 🧰 How it Works

1. When an event is marked as `LIVE`, it's stored in an in-memory map.
2. A scheduled task polls the external API (via Feign) for each active event.
3. If a valid response is received, it is published to the configured Kafka topic.
4. Publishing includes retry logic (up to 3 attempts with delay).
5. Failed polls or Kafka errors are logged and do not crash the scheduler.

---

## 🐳 Running the Project

Ensure `kafka`, `zookeeper`, `kafka-ui` and `mock-api` services are configured in `docker-compose.yml`:

```bash ./track-sports 
docker-compose up
```

```
Use Swagger to trigger the endpoint `/events/status`, acess the url: http://localhost:8080/swagger-ui/index.html#
```

```
Check the events in the topic using the kafta graphical interface, access the url: http://localhost:8082

topics/
├── live-events/
│   ├── messages
```
---
