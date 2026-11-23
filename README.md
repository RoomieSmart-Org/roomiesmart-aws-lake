# 🏠 RoomieSmart AWS Lake

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![AWS](https://img.shields.io/badge/AWS-Cloud%20Native-232F3E?logo=amazonaws)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Bus-231F20?logo=apachekafka)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Task%20Queue-FF6600?logo=rabbitmq)
![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC?logo=terraform)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?logo=docker)

Cloud-native, event-driven (EDA) distributed platform for automated management of **co-living** residential buildings. It solves unfair utility bill splitting and coexistence conflicts through real-time IoT telemetry, exact billing based on physical consumption, and objective violation resolution powered by Artificial Intelligence (**Amazon Bedrock**).

---

## 🏛️ Architecture Pillars

- **Hybrid Event-Driven Architecture (EDA):** Full decoupling through **Apache Kafka** (high-throughput IoT telemetry ingestion) and **RabbitMQ** (transactional task queues with *Dead-Letter Queues*).
- **CQRS & Polyglot Persistence:** Strict separation of analytical reads and operational writes distributed across **11 specialized data stores** (Amazon Aurora, RDS, DynamoDB, S3/Athena Data Lake, ElastiCache Redis, DocumentDB, Keyspaces, Supabase, among others).
- **Hexagonal Architecture:** Each Java Spring Boot microservice isolates domain logic from infrastructure and persistence adapters.
- **Ephemeral Deployment (*Burst Deployment*):** 100% Infrastructure as Code with **Terraform**, deployed on **Amazon EKS / AWS Fargate** on demand for academic evaluation at $0 USD out-of-pocket cost using AWS Academy Learner Lab credits.

---

## 📂 Monorepo Structure

```text
roomiesmart-aws-lake/
├── .github/
│   ├── workflows/                 # Modular CI/CD pipelines per microservice
│   └── pull_request_template.md   # PR template linked to Jira
├── docs/                          # SAD, ATAM/QuaDAI evaluation, and diagrams
├── infrastructure/
│   ├── terraform/                 # IaC modules (VPC, EKS, Fargate, ECR, DBs)
│   └── docker-compose.local.yml   # Local stack (PostgreSQL, Redis, RabbitMQ, Kafka)
├── services/                      # 10 Java Spring Boot Microservices
│   ├── ms-01-iot-ingestion/
│   ├── ms-02-analytics/
│   ├── ms-03-identity-auth/
│   ├── ms-04-property-catalog/
│   ├── ms-05-billing/
│   ├── ms-06-ai-orchestrator/
│   ├── ms-07-notifications/
│   ├── ms-08-datalake-etl/
│   ├── ms-09-audit-ledger/
│   └── ms-10-reporting-n8n/
├── .gitignore
└── README.md
```

---

## 🧩 Microservices Catalog & Roadmap (Scrum)

| ID | Microservice | Responsibility | Persistence / Broker | Sprint | Status |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **MS-03** | `ms-03-identity-auth` | JWT authentication, RBAC/ABAC, and session revocation | Redis / API Gateway | Sprint 1 | 🚧 In Progress |
| **MS-04** | `ms-04-property-catalog` | Management of buildings, rooms, sensors, and leases | Supabase / RDS PostgreSQL | Sprint 1 | 🚧 In Progress |
| **MS-01** | `ms-01-iot-ingestion` | Raw noise (dB) and energy (kWh) telemetry ingestion | MQTT → Kafka / DynamoDB | Sprint 2 | ⏳ Pending |
| **MS-08** | `ms-08-datalake-etl` | Apache Parquet compression and historical archiving | Kafka → Amazon S3 | Sprint 2 | ⏳ Pending |
| **MS-06** | `ms-06-ai-orchestrator` | Violation evaluation with Amazon Bedrock + Circuit Breaker | Kafka → RabbitMQ / DocumentDB | Sprint 3 | ⏳ Pending |
| **MS-07** | `ms-07-notifications` | Alert dispatching with anti-spam *Rate Limiting* | RabbitMQ / Redis | Sprint 3 | ⏳ Pending |
| **MS-05** | `ms-05-billing` | ACID fractional billing and *Idempotent Consumer* | Kafka / RabbitMQ → Aurora | Sprint 4 | ⏳ Pending |
| **MS-09** | `ms-09-audit-ledger` | Immutable logging of critical events for auditing | Kafka → Amazon Keyspaces | Sprint 4 | ⏳ Pending |
| **MS-02** | `ms-02-analytics` | GraphQL API and serverless analytical queries (CQRS) | Amazon S3 + Athena | Sprint 5 | ⏳ Pending |
| **MS-10** | `ms-10-reporting-n8n` | Monthly PDF reports and cold backups (*Disaster Recovery*) | n8n / On-Premise VPN | Sprint 5 | ⏳ Pending |

---

## 🚀 Quick Start (Local Environment)

### Prerequisites
- **Java JDK 21** and **Maven 3.9+**
- **Docker** and **Docker Compose**
- **Terraform 1.7+** (for AWS deployments)

### 1. Start local infrastructure
Spin up PostgreSQL, Redis, RabbitMQ, and Apache Kafka without consuming AWS credits:

```bash
docker compose -f infrastructure/docker-compose.local.yml up -d
```

- **RabbitMQ Management UI:** `http://localhost:15672` (`guest` / `guest`)
- **PostgreSQL:** `localhost:5432` (`rs_admin` / `rs_password`, DB: `roomiesmart_db`)
- **Redis:** `localhost:6379`
- **Apache Kafka:** `localhost:9092`

### 2. Run a microservice

```bash
cd services/ms-03-identity-auth
mvn spring-boot:run
```

### 3. Stop local environment

```bash
docker compose -f infrastructure/docker-compose.local.yml down
```

---

## 🔄 GitFlow, CI/CD & Jira Workflow

This repository is integrated with **Jira Software** via *GitHub for Jira* for traceability and Scrum board automation:

- **Main branches:** `main` (production / CD deployment to AWS ECR) and `develop` (Sprint integration).
- **Branch naming convention:** `feature/RS-<ID>-<short-description>` (automatically transitions the ticket to *In Progress*).
- **Commit convention:** `RS-<ID> <type>(<service>): <message>`  
  *Example:* `RS-15 feat(ms-03): implement JWT generation and Redis blacklist`
- **Pull Requests:** Every PR targeting `develop` or `main` automatically triggers the **GitHub Actions** pipeline for the modified microservice (unit tests + `Dockerfile` verification) and moves the Jira ticket to *In Review* (and to *Done* upon *merge*).

---

## 📚 Project Documentation

- **Confluence (SAD & C4/UML Diagrams):** `[Link to your Confluence space]`
- **Scrum Board (Jira):** `[Link to your Jira project]`
- **Architecture Evaluation (ATAM & QuaDAI):** Available in the `/docs` folder

---

## 👨‍💻 Author

**Jonathan David Plaza Nieto**  
Information Systems Engineering — Universidad Central del Ecuador (UCE)# 🏠 RoomieSmart AWS Lake

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![AWS](https://img.shields.io/badge/AWS-Cloud%20Native-232F3E?logo=amazonaws)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Bus-231F20?logo=apachekafka)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Task%20Queue-FF6600?logo=rabbitmq)
![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC?logo=terraform)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?logo=docker)

Cloud-native, event-driven (EDA) distributed platform for automated management of **co-living** residential buildings. It solves unfair utility bill splitting and coexistence conflicts through real-time IoT telemetry, exact billing based on physical consumption, and objective violation resolution powered by Artificial Intelligence (**Amazon Bedrock**).

---

## 🏛️ Architecture Pillars

- **Hybrid Event-Driven Architecture (EDA):** Full decoupling through **Apache Kafka** (high-throughput IoT telemetry ingestion) and **RabbitMQ** (transactional task queues with *Dead-Letter Queues*).
- **CQRS & Polyglot Persistence:** Strict separation of analytical reads and operational writes distributed across **11 specialized data stores** (Amazon Aurora, RDS, DynamoDB, S3/Athena Data Lake, ElastiCache Redis, DocumentDB, Keyspaces, Supabase, among others).
- **Hexagonal Architecture:** Each Java Spring Boot microservice isolates domain logic from infrastructure and persistence adapters.
- **Ephemeral Deployment (*Burst Deployment*):** 100% Infrastructure as Code with **Terraform**, deployed on **Amazon EKS / AWS Fargate** on demand for academic evaluation at $0 USD out-of-pocket cost using AWS Academy Learner Lab credits.

---

## 📂 Monorepo Structure

```text
roomiesmart-aws-lake/
├── .github/
│   ├── workflows/                 # Modular CI/CD pipelines per microservice
│   └── pull_request_template.md   # PR template linked to Jira
├── docs/                          # SAD, ATAM/QuaDAI evaluation, and diagrams
├── infrastructure/
│   ├── terraform/                 # IaC modules (VPC, EKS, Fargate, ECR, DBs)
│   └── docker-compose.local.yml   # Local stack (PostgreSQL, Redis, RabbitMQ, Kafka)
├── services/                      # 10 Java Spring Boot Microservices
│   ├── ms-01-iot-ingestion/
│   ├── ms-02-analytics/
│   ├── ms-03-identity-auth/
│   ├── ms-04-property-catalog/
│   ├── ms-05-billing/
│   ├── ms-06-ai-orchestrator/
│   ├── ms-07-notifications/
│   ├── ms-08-datalake-etl/
│   ├── ms-09-audit-ledger/
│   └── ms-10-reporting-n8n/
├── .gitignore
└── README.md
```

---

## 🧩 Microservices Catalog & Roadmap (Scrum)

| ID | Microservice | Responsibility | Persistence / Broker | Sprint | Status |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **MS-03** | `ms-03-identity-auth` | JWT authentication, RBAC/ABAC, and session revocation | Redis / API Gateway | Sprint 1 | 🚧 In Progress |
| **MS-04** | `ms-04-property-catalog` | Management of buildings, rooms, sensors, and leases | Supabase / RDS PostgreSQL | Sprint 1 | 🚧 In Progress |
| **MS-01** | `ms-01-iot-ingestion` | Raw noise (dB) and energy (kWh) telemetry ingestion | MQTT → Kafka / DynamoDB | Sprint 2 | ⏳ Pending |
| **MS-08** | `ms-08-datalake-etl` | Apache Parquet compression and historical archiving | Kafka → Amazon S3 | Sprint 2 | ⏳ Pending |
| **MS-06** | `ms-06-ai-orchestrator` | Violation evaluation with Amazon Bedrock + Circuit Breaker | Kafka → RabbitMQ / DocumentDB | Sprint 3 | ⏳ Pending |
| **MS-07** | `ms-07-notifications` | Alert dispatching with anti-spam *Rate Limiting* | RabbitMQ / Redis | Sprint 3 | ⏳ Pending |
| **MS-05** | `ms-05-billing` | ACID fractional billing and *Idempotent Consumer* | Kafka / RabbitMQ → Aurora | Sprint 4 | ⏳ Pending |
| **MS-09** | `ms-09-audit-ledger` | Immutable logging of critical events for auditing | Kafka → Amazon Keyspaces | Sprint 4 | ⏳ Pending |
| **MS-02** | `ms-02-analytics` | GraphQL API and serverless analytical queries (CQRS) | Amazon S3 + Athena | Sprint 5 | ⏳ Pending |
| **MS-10** | `ms-10-reporting-n8n` | Monthly PDF reports and cold backups (*Disaster Recovery*) | n8n / On-Premise VPN | Sprint 5 | ⏳ Pending |

---

## 🚀 Quick Start (Local Environment)

### Prerequisites
- **Java JDK 21** and **Maven 3.9+**
- **Docker** and **Docker Compose**
- **Terraform 1.7+** (for AWS deployments)

### 1. Start local infrastructure
Spin up PostgreSQL, Redis, RabbitMQ, and Apache Kafka without consuming AWS credits:

```bash
docker compose -f infrastructure/docker-compose.local.yml up -d
```

- **RabbitMQ Management UI:** `http://localhost:15672` (`guest` / `guest`)
- **PostgreSQL:** `localhost:5432` (`rs_admin` / `rs_password`, DB: `roomiesmart_db`)
- **Redis:** `localhost:6379`
- **Apache Kafka:** `localhost:9092`

### 2. Run a microservice

```bash
cd services/ms-03-identity-auth
mvn spring-boot:run
```

### 3. Stop local environment

```bash
docker compose -f infrastructure/docker-compose.local.yml down
```

---

## 🔄 GitFlow, CI/CD & Jira Workflow

This repository is integrated with **Jira Software** via *GitHub for Jira* for traceability and Scrum board automation:

- **Main branches:** `main` (production / CD deployment to AWS ECR) and `develop` (Sprint integration).
- **Branch naming convention:** `feature/RS-<ID>-<short-description>` (automatically transitions the ticket to *In Progress*).
- **Commit convention:** `RS-<ID> <type>(<service>): <message>`  
  *Example:* `RS-15 feat(ms-03): implement JWT generation and Redis blacklist`
- **Pull Requests:** Every PR targeting `develop` or `main` automatically triggers the **GitHub Actions** pipeline for the modified microservice (unit tests + `Dockerfile` verification) and moves the Jira ticket to *In Review* (and to *Done* upon *merge*).

---

## 📚 Project Documentation

- **Confluence (SAD & C4/UML Diagrams):** `[Link to your Confluence space]`
- **Scrum Board (Jira):** `[Link to your Jira project]`
- **Architecture Evaluation (ATAM & QuaDAI):** Available in the `/docs` folder

---

## 👨‍💻 Author

**Jonathan David Plaza Nieto**  
Information Systems Engineering — Universidad Central del Ecuador (UCE)
