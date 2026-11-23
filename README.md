# 🏠 RoomieSmart AWS Lake

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=springboot)
![AWS](https://img.shields.io/badge/AWS-Cloud%20Native-232F3E?logo=amazonaws)
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-Event%20Bus-231F20?logo=apachekafka)
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Task%20Queue-FF6600?logo=rabbitmq)
![Terraform](https://img.shields.io/badge/Terraform-IaC-7B42BC?logo=terraform)
![Docker](https://img.shields.io/badge/Docker-Containers-2496ED?logo=docker)

Plataforma distribuida *cloud-native* orientada a eventos (EDA) para la gestión automatizada de edificios residenciales bajo el modelo **co-living**. Resuelve la división injusta de servicios básicos y los conflictos de convivencia mediante telemetría IoT en tiempo real, facturación exacta basada en consumo físico y resolución objetiva de infracciones impulsada por Inteligencia Artificial (**Amazon Bedrock**).

---

## 🏛️ Pilares de la Arquitectura

* **Arquitectura Orientada a Eventos (EDA) Híbrida:** Desacoplamiento total mediante **Apache Kafka** (ingesta masiva de telemetría IoT) y **RabbitMQ** (colas de tareas transaccionales con *Dead-Letter Queues*).
* **CQRS y Persistencia Políglota:** Separación estricta de lecturas analíticas y escrituras operativas distribuidas en **11 almacenes de datos especializados** (Amazon Aurora, RDS, DynamoDB, S3/Athena Data Lake, ElastiCache Redis, DocumentDB, Keyspaces, Supabase, entre otros).
* **Arquitectura Hexagonal:** Cada microservicio en Java Spring Boot separa la lógica de dominio de los adaptadores de infraestructura y persistencia.
* **Despliegue Efímero (*Burst Deployment*):** Infraestructura 100% definida como código con **Terraform** y desplegada en **Amazon EKS / AWS Fargate** bajo demanda para validación académica a costo real de $0 USD usando créditos de AWS Academy Learner Lab.

---

## 📂 Estructura del Monorepositorio

```text
roomiesmart-aws-lake/
├── .github/
│   ├── workflows/                 # Pipelines CI/CD modulares por microservicio
│   └── pull_request_template.md   # Plantilla de PR vinculada a Jira
├── docs/                          # SAD, evaluación ATAM/QuaDAI y diagramas
├── infrastructure/
│   ├── terraform/                 # Módulos IaC (VPC, EKS, Fargate, ECR, BD)
│   └── docker-compose.local.yml   # Stack local (PostgreSQL, Redis, RabbitMQ, Kafka)
├── services/                      # 10 Microservicios Java Spring Boot
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

## 🧩 Catálogo de Microservicios y Roadmap (Scrum)

| ID | Microservicio | Responsabilidad | Persistencia / Broker | Sprint | Estado |
| :--- | :--- | :--- | :--- | :---: | :---: |
| **MS-03** | `ms-03-identity-auth` | Autenticación JWT, RBAC/ABAC y revocación de sesiones | Redis / API Gateway | Sprint 1 | 🚧 En desarrollo |
| **MS-04** | `ms-04-property-catalog` | Gestión de edificios, habitaciones, sensores y contratos | Supabase / RDS PostgreSQL | Sprint 1 | 🚧 En desarrollo |
| **MS-01** | `ms-01-iot-ingestion` | Ingesta de telemetría cruda de ruido (dB) y energía (kWh) | MQTT → Kafka / DynamoDB | Sprint 2 | ⏳ Pendiente |
| **MS-08** | `ms-08-datalake-etl` | Compresión Apache Parquet y archivado histórico | Kafka → Amazon S3 | Sprint 2 | ⏳ Pendiente |
| **MS-06** | `ms-06-ai-orchestrator` | Evaluación de infracciones con Amazon Bedrock + Circuit Breaker | Kafka → RabbitMQ / DocumentDB | Sprint 3 | ⏳ Pendiente |
| **MS-07** | `ms-07-notifications` | Despacho de alertas con *Rate Limiting* anti-spam | RabbitMQ / Redis | Sprint 3 | ⏳ Pendiente |
| **MS-05** | `ms-05-billing` | Facturación fraccionada ACID e *Idempotent Consumer* | Kafka / RabbitMQ → Aurora | Sprint 4 | ⏳ Pendiente |
| **MS-09** | `ms-09-audit-ledger` | Registro inmutable de eventos críticos para auditoría | Kafka → Amazon Keyspaces | Sprint 4 | ⏳ Pendiente |
| **MS-02** | `ms-02-analytics` | API GraphQL y consultas analíticas *serverless* (CQRS) | Amazon S3 + Athena | Sprint 5 | ⏳ Pendiente |
| **MS-10** | `ms-10-reporting-n8n` | Reportes mensuales PDF y respaldos en frío (*Disaster Recovery*) | n8n / On-Premise VPN | Sprint 5 | ⏳ Pendiente |

---

## 🚀 Inicio Rápido (Entorno Local)

### Prerrequisitos
* **Java JDK 21** y **Maven 3.9+**
* **Docker** y **Docker Compose**
* **Terraform 1.7+** (para despliegues en AWS)

### 1. Levantar la infraestructura local
Inicia PostgreSQL, Redis, RabbitMQ y Apache Kafka sin consumir créditos de AWS:

```bash
docker compose -f infrastructure/docker-compose.local.yml up -d
```

* **RabbitMQ Management UI:** `http://localhost:15672` (`guest` / `guest`)
* **PostgreSQL:** `localhost:5432` (`rs_admin` / `rs_password`, DB: `roomiesmart_db`)
* **Redis:** `localhost:6379`
* **Apache Kafka:** `localhost:9092`

### 2. Ejecutar un microservicio

```bash
cd services/ms-03-identity-auth
mvn spring-boot:run
```

### 3. Detener el entorno local

```bash
docker compose -f infrastructure/docker-compose.local.yml down
```

---

## 🔄 Flujo de Trabajo GitFlow, CI/CD y Jira

Este repositorio está integrado con **Jira Software** mediante *GitHub for Jira* para trazabilidad y automatización del tablero Scrum:

* **Ramas principales:** `main` (producción / despliegue CD a AWS ECR) y `develop` (integración por Sprint).
* **Convención de ramas:** `feature/RS-<ID>-<descripcion-corta>` (transiciona automáticamente el ticket a *In Progress*).
* **Convención de commits:** `RS-<ID> <tipo>(<servicio>): <mensaje>`  
  *Ejemplo:* `RS-15 feat(ms-03): implement JWT generation and Redis blacklist`
* **Pull Requests:** Todo PR hacia `develop` o `main` dispara automáticamente el pipeline de **GitHub Actions** del microservicio modificado (pruebas unitarias + verificación de `Dockerfile`) y mueve el ticket en Jira a *In Review* (y a *Done* tras el *merge*).

---

## 📚 Documentación del Proyecto

* **Confluence (SAD y Diagramas C4/UML):** `[Enlace a tu espacio de Confluence]`
* **Tablero Scrum (Jira):** `[Enlace a tu proyecto de Jira]`
* **Evaluación de Arquitectura (ATAM & QuaDAI):** Disponible en la carpeta `/docs`

---

## 👨‍💻 Autor

**Luis André Luna Palacios**  
Ingeniería en Sistemas de Información — Universidad Central del Ecuador (UCE)