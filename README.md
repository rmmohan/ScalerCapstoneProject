# E-Commerce Microservices Project

## 📌 Overview

This repository contains a **demo e-commerce backend system** developed as part of an academic capstone project for the **Master of Science in Computer Science** program.  
The primary goal of this project is to **demonstrate backend engineering concepts and microservices architecture**, rather than to deliver a fully production-ready e-commerce platform.

The system showcases modern backend practices such as microservices, containerization, authentication, synchronous and asynchronous communication, resilience patterns, observability, and local infrastructure orchestration.

---

## 🎯 Project Objectives

- Demonstrate **microservices-based system design**
- Implement **service-to-service communication**
- Showcase **event-driven architecture**
- Integrate **authentication and authorization**
- Deploy services using **Docker Compose**
- Demonstrate **resilience and observability concepts**
- Provide **live system validation** via video demonstration

---

## 🧱 System Architecture

The system is built using a **microservices architecture** with the following components:

### Backend Services
- **Product Service** – Manages product catalog (MongoDB)
- **Order Service** – Handles order requests (MySQL)
- **Inventory Service** – Validates stock availability (MySQL)
- **Notification Service** – Sends email notifications
- **API Gateway** – Central entry point for client requests

### Supporting Infrastructure
- **Keycloak** – Authentication and Authorization
- **Apache Kafka** – Asynchronous messaging
- **MySQL & MongoDB** – Databases (polyglot persistence)
- **Prometheus, Loki, Tempo, Grafana** – Observability stack
- **Nginx + SSL** – Secure communication

> 📌 Note: The architecture is intentionally simplified to focus on **concept demonstration**, not production optimization.

---

## 🛠️ Technology Stack

### Backend
- Java 24
- Spring Boot
- Spring Cloud OpenFeign
- Spring Security
- Spring Mail
- Flyway

### Frontend
- React

### Databases
- MongoDB (Product Service)
- MySQL (Order & Inventory Services)

### Messaging
- Apache Kafka

### Security
- Keycloak (OAuth 2.0 / OpenID Connect)
- JWT-based authentication

### DevOps & Observability
- Docker & Docker Compose
- Prometheus
- Grafana
- Loki
- Tempo

---

## 🔁 Communication Patterns

- **Synchronous Communication**
  - OpenFeign (Order → Inventory / Payment)
- **Asynchronous Communication**
  - Kafka events (Payment confirmation, notifications)

---

## 🛡️ Resilience

- Circuit Breaker pattern implemented for synchronous service calls
- Prevents cascading failures during service unavailability
- Configuration is intentionally basic for study purposes

---

## 🔍 Observability

The project demonstrates **full-stack observability**:
- Metrics via Prometheus
- Logs via Loki
- Distributed tracing via Tempo
- Unified visualization using Grafana

---

## 🧪 Testing Strategy

This is a **demonstration-focused project**, not a production system.

- ❌ No unit tests
- ✅ 1–2 integration tests using **Testcontainers**
- Manual verification via:
  - API calls
  - Frontend interactions
  - Logs and dashboards

Testing scope is intentionally limited and documented transparently.

---

## 🚀 Running the Project Locally

### Prerequisites
- Docker
- Docker Compose
- Java 24 (optional, for local dev)
- Node.js & npm (optional, for frontend dev)

### Start the System
```bash
docker compose up -d
