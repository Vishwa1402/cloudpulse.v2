# 🌌 NexusOps — AI-Powered Incident Command Center & Observability Platform

NexusOps is a production-grade, enterprise-ready cloud observability and real-time incident response platform. Built using **Spring Boot (Java 21)** and **Angular 21**, it monitors distributed application metrics, detects infrastructure anomalies, and provides contextualized, telemetry-grounded AI diagnostic assistance.

---

## 🚀 Architectural Overview

NexusOps is designed around a **clean, layered MVC architecture** with strict separation of concerns, transactional integrity, and fault-tolerant system boundaries.

```mermaid
graph TD
    A[Angular 21 Standalone App] <-->|WebSocket STOMP / REST| B[Spring Boot Gateway / API]
    B <-->|Spring Security JWT / RBAC| C[Core Services]
    C -->|Micrometer Prometheus / Reflective Fallback| D[Scrape Target / Actuator]
    C <-->|Spring Data JPA| E[PostgreSQL DB]
    C <-->|Grounded LLM Prompt Context| F[AIOps Diagnostic Copilot]
```

### Core Architectural Features:
*   **Layered Security (JWT & RBAC):** End-to-end token validation with state-isolated multi-tenant structures (`organizations`, `users`, `projects`, `services`).
*   **High-Fidelity Telemetry Scraper:** Periodically polls target services every 5 seconds. Connects directly to Prometheus, with an advanced fallback scraping JVM MXBean metrics and parsing raw Micrometer actuator outputs in the event of telemetry platform downtime.
*   **Event-Driven Rule Engine:** Evaluates scrape metrics against strict SLA thresholds. Instantly spawns incidents, logs immutable audit logs, triggers email/channel notifications, and broadcasts status changes over WebSocket brokers.
*   **Grounded AIOps Co-pilot:** Integrates an AI command assistant that retrieves live metrics context to diagnose root-causes, strictly separating facts (`[FACT]`) from diagnostic hypotheses (`[SUGGESTION]`).

---

## 🛠️ Technology Stack

| Component | Technology | Description |
|---|---|---|
| **Backend Framework** | Spring Boot 4.0.6 | Bleeding-edge microservices & API foundation |
| **Language** | Java 21 | Utilizes modern OOP & modern compiler features |
| **Security** | Spring Security, JJWT (v0.12.5) | Stateless JWT with Role-Based Access Control |
| **Frontend Framework** | Angular 21 (v21.2.0) | Standalone reactive component architecture |
| **UI Styling** | Tailwind CSS 4.0 | Premium modern design system |
| **Data Charts** | ApexCharts | High-fidelity interactive dashboards |
| **Database** | PostgreSQL | Enterprise relational storage & time-series indices |
| **Real-time Comms** | WebSockets (STOMP / SockJS) | Sub-100ms bi-directional state broadcast |
| **Telemetry System** | Prometheus, Micrometer Actuator | Production-level server instrumentation |
| **Containerization** | Docker, Docker Compose | Consistent multi-container orchestration |

---

## ⚡ Key Features

### 1. Unified Real-Time Dashboard
*   Interactive multi-series charts graphing live CPU, Memory, Request Rate (RPS), and HTTP Error Rates.
*   Automated WebSocket-driven UI synchronization—refreshes status counters instantly when state transitions occur.
*   Clean loading and error state boundaries with responsive design.

### 2. Automated Incident Lifecycle
*   Anomalies automatically trigger incidents based on strict rules (e.g., CPU > 80%, RAM > 90%, Error Rate > 1.0%).
*   Incident statuses track standard operational states (`ACTIVE`, `INVESTIGATING`, `RESOLVED`, `CLOSED`).
*   Manual operator override allows forced resolution with automated STOMP-based client broadcasts and system-wide audit logging.

### 3. Fault-Tolerant Observability
*   The metric polling engine features a robust **resilient fallback**.
*   If Prometheus goes offline, the collector catches the error and executes reflective calls on `OperatingSystemMXBean` and scrapes direct local Actuator string metrics, maintaining incident detection and telemetry-grounded AI capabilities with zero downtime.

### 4. Telemetry-Grounded AI Command Assistant
*   Natural Language Processing interface allowing operators to ask questions such as *"Why did CPU spike?"*, *"Analyze system memory"*, or *"What is the overall system health?"*.
*   Grounded RAG design: Extracts live state variables from Actuator/Prometheus, preventing LLM hallucinations and delivering structured, metric-accurate summaries.

---

## 🗄️ Database Schema Design

NexusOps features a normalized PostgreSQL schema designed for high-performance indexing and multi-tenant data isolation:

*   **Multi-Tenancy:** `organizations`, `projects`, and `services` map hierarchical ownership.
*   **Time-Series Metric snapshots:** `service_metrics` stores fast telemetry historical trends.
*   **Observability:** `service_health` logs historical ping latency and status checks.
*   **Incident Logs:** `incidents`, `incident_status_history`, and `incident_events` maintain a secure, immutable audit trail.
*   **Enterprise Integration:** `audit_logs`, `api_keys`, and `notification_channels` support secure communication integrations.

---

## 🚦 Getting Started

### Prerequisites
*   **Java 21 JDK** installed.
*   **Node.js (v18+) & npm** installed.
*   **PostgreSQL** installed and running.
*   **Docker & Docker Compose** (Optional, for Prometheus integration).

### Step 1: Database Setup
1.  Create a PostgreSQL database named `cloudpulse`:
    ```sql
    CREATE DATABASE cloudpulse;
    ```
2.  Update database credentials in the backend application configuration properties:
    `backend/src/main/resources/application.properties`.

### Step 2: Running the Backend
1.  Navigate to the backend directory:
    ```bash
    cd backend
    ```
2.  Build and run the Spring Boot application using the Maven wrapper:
    ```bash
    ./mvnw spring-boot:run
    ```
    The API server will bootstrap on port `8080`.

### Step 3: Running the Frontend
1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```
2.  Install dependencies:
    ```bash
    npm install
    ```
3.  Launch the Angular development server:
    ```bash
    npm run start
    ```
    Open your browser and navigate to `http://localhost:4200`.

---

## 🧪 Testing and Quality Assurance

NexusOps maintains a high-quality codebase with **Test-Driven Development (TDD)** principles. The backend contains a robust suite of unit and integration tests using **JUnit 5** and **Mockito** with 100% build verification.

### Running Backend Tests
Execute the test suite using Maven:
```bash
cd backend
./mvnw test
```
**Test Scope:**
*   `PrometheusServiceTest`: Validates telemetry polling and Actuator fallback resilience.
*   `IncidentDetectionServiceTest`: Verifies automated threshold breaches, database saves, and WebSocket refresh triggers.
*   `DashboardControllerTest` & `DashboardServiceTest`: Checks API response payload consistency.
*   `AiAnalysisServiceTest` & `AiChatControllerTest`: Assures LLM grounding and query safety.

---

## 🐳 Docker Deployment

The environment can be orchestrated easily via Docker Compose.
1.  To spin up the centralized Postgres, Prometheus, and Actuator targets, run:
    ```bash
    docker-compose up -d
    ```
2.  Prometheus dashboard will be available at `http://localhost:9090`.