🏠 RoomieSmart AWS Lake

!Java !Spring Boot !AWS !Apache Kafka !RabbitMQ !Terraform !Docker

A distributed, cloud-native, event-driven architecture (EDA) platform designed for the automated management of residential buildings under the co-living model. It addresses unfair utility cost allocation and roommate conflicts through real-time IoT telemetry, accurate billing based on actual consumption, and objective violation resolution powered by Artificial Intelligence (Amazon Bedrock).

🏛️ Architecture Pillars
Hybrid Event-Driven Architecture (EDA): Complete decoupling through Apache Kafka (high-volume IoT telemetry ingestion) and RabbitMQ (transactional task queues with Dead-Letter Queues).
CQRS and Polyglot Persistence: Strict separation of analytical reads and operational writes across 11 specialized data stores (Amazon Aurora, RDS, DynamoDB, S3/Athena Data Lake, ElastiCache Redis, DocumentDB, Keyspaces, Supabase, among others).
Hexagonal Architecture: Each Java Spring Boot microservice separates domain logic from infrastructure and persistence adapters.
Ephemeral Deployment (Burst Deployment): Infrastructure fully defined as code using Terraform and deployed on Amazon EKS / AWS Fargate on demand, enabling validation for academic purposes at a real cost of $0 USD using AWS Academy Learner Lab credits.
📂 Monorepository Structure
text
roomiesmart-aws-lake/
├── .github/
│   ├── workflows/                 # Modular CI/CD pipelines per microservice
│   └── pull_request_template.md   # Jira-linked PR template
├── docs/                          # SAD, ATAM/QuaDAI assessments, and diagrams
├── infrastructure/
│   ├── terraform/                 # IaC modules (VPC, EKS, Fargate, ECR, DB)
│   └── docker-compose.local.yml   # Local stack (PostgreSQL, Redis, RabbitMQ, Kafka)
├── services/                      # 10 Java Spring Boot microservices
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

🧩 Microservices Catalog and Roadmap (Scrum)
ID	Microservice	Responsibility	Persistence / Broker	Sprint	Status
MS-03	ms-03-identity-auth	JWT authentication, RBAC/ABAC, and session revocation	Redis / API Gateway	Sprint 1	🚧 In Progress
MS-04	ms-04-property-catalog	Management of buildings, rooms, sensors, and contracts	Supabase / RDS PostgreSQL	Sprint 1	🚧 In Progress
MS-01	ms-01-iot-ingestion	Raw noise (dB) and energy (kWh) telemetry ingestion	MQTT → Kafka / DynamoDB	Sprint 2	⏳ Pending
MS-08	ms-08-datalake-etl	Apache Parquet compression and historical data archiving	Kafka → Amazon S3	Sprint 2	⏳ Pending
MS-06	ms-06-ai-orchestrator	Violation assessment using Amazon Bedrock + Circuit Breaker	Kafka → RabbitMQ / DocumentDB	Sprint 3	⏳ Pending
MS-07	ms-07-notifications	Alert dispatching with anti-spam Rate Limiting	RabbitMQ / Redis	Sprint 3	⏳ Pending
MS-05	ms-05-billing	ACID-compliant split billing and Idempotent Consumer	Kafka / RabbitMQ → Aurora	Sprint 4	⏳ Pending
MS-09	ms-09-audit-ledger	Immutable recording of critical events for auditing	Kafka → Amazon Keyspaces	Sprint 4	⏳ Pending
MS-02	ms-02-analytics	GraphQL API and serverless analytical queries (CQRS)	Amazon S3 + Athena	Sprint 5	⏳ Pending
MS-10	ms-10-reporting-n8n	Monthly PDF reports and cold backups (Disaster Recovery)	n8n / On-Premise VPN	Sprint 5	⏳ Pending
🚀 Quick Start (Local Environment)
Prerequisites
Java JDK 21 and Maven 3.9+
Docker and Docker Compose
Terraform 1.7+ (for AWS deployments)
1. Start the Local Infrastructure

Launch PostgreSQL, Redis, RabbitMQ, and Apache Kafka without consuming AWS credits:

bash
docker compose -f infrastructure/docker-compose.local.yml up -d

RabbitMQ Management UI: http://localhost:15672 (guest / guest)
PostgreSQL: localhost:5432 (rs_admin / rs_password, DB: roomiesmart_db)
Redis: localhost:6379
Apache Kafka: localhost:9092
2. Run a Microservice
bash
cd services/ms-03-identity-auth
mvn spring-boot:run

3. Stop the Local Environment
bash
docker compose -f infrastructure/docker-compose.local.yml down

🔄 GitFlow, CI/CD, and Jira Workflow

This repository is integrated with Jira Software through GitHub for Jira, enabling traceability and automation of the Scrum board:

Main branches: main (production / CD deployment to AWS ECR) and develop (Sprint integration).
Branch naming convention: feature/RS-<ID>-<short-description> (automatically transitions the ticket to In Progress).
Commit convention: RS-<ID> <type>(<service>): <message>
Example: RS-15 feat(ms-03): implement JWT generation and Redis blacklist
Pull Requests: Every PR targeting develop or main automatically triggers the GitHub Actions pipeline for the modified microservice (unit tests + Dockerfile validation) and moves the Jira ticket to In Review (and to Done after the merge).
📚 Project Documentation
Confluence (SAD and C4/UML Diagrams): [Link to your Confluence space]
Scrum Board (Jira): [Link to your Jira project]
Architecture Assessment (ATAM & QuaDAI): Available in the /docs directory.
👨‍💻 Author

Jonathan David Plaza Nieto
Information Systems Engineering — Central University of Ecuador (UCE)
