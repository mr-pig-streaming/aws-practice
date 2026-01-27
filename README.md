# Cloud Backend Service (Java + AWS)

A minimal, production-oriented backend service built with **Java** and deployed on **AWS**. This project demonstrates core backend engineering and cloud fundamentals, including REST API design, persistence, CI/CD, and basic operational concerns, while remaining intentionally lightweight and cost-efficient.

The service exposes RESTful endpoints for ingesting, storing, and querying structured data and is designed to reflect realistic patterns used by mid-sized engineering teams.

---

## Project Goals

This project was built to:

* Demonstrate backend service design using Java
* Gain hands-on experience with AWS compute, networking, and storage
* Practice deploying and operating a cloud-hosted service
* Showcase production-minded engineering decisions without over-engineering

---

## Architecture Overview

**Backend:**

* Java 17
* Spring Boot (REST API, configuration, dependency injection)

**Cloud Infrastructure (AWS):**

* EC2 (t3.micro, Free Tier–eligible)
* RDS (PostgreSQL)

**CI/CD:**

* GitHub Actions for build, test, and deployment

**Other:**

* Maven for dependency management
* JUnit for automated testing
* JSON-based API contracts

---

## API Functionality

The service provides a simple REST API:

* `POST /records`
  Ingest a JSON payload and persist it to the database.

* `GET /records/{id}`
  Retrieve a previously stored record by ID.

* `GET /records?filter=`
  Query records using basic filter parameters.

Input validation, structured error responses, and basic logging are implemented throughout the service.

---

## Data Model

Records are stored with:

* Unique identifier
* JSON payload
* Timestamps (created / updated)

The data model is intentionally simple to keep the focus on service design, persistence, and cloud deployment patterns.

---

## Local Development

### Prerequisites

* Java 17+
* Maven


---

## Testing

* Unit tests are written using **JUnit**
* Service and repository layers are tested independently
* Tests are executed automatically as part of the CI pipeline

Run tests locally with:

```bash
mvn test
```

---

## Cloud Deployment (AWS)

### Infrastructure

* EC2 instance hosted in a single VPC with a public subnet
* IAM role attached to EC2 with least-privilege permissions
* Application configured via environment variables

### Deployment Flow

1. Code pushed to `main` branch
2. GitHub Actions pipeline runs:

   * Compile
   * Run tests
   * Package application
3. Artifact deployed to EC2
4. Application restarted with updated build

This setup prioritizes simplicity, transparency, and cost control.

---

## Cost Management

The project is designed to run within the AWS Free Tier:

* EC2 t3.micro
* Minimal storage usage
* No managed Kubernetes or high-cost services

Instances can be stopped when not in use to reduce costs further.

---

## Design Decisions & Trade-offs

* **EC2 over Kubernetes:** Chosen for simplicity and cost efficiency while still demonstrating cloud fundamentals.
* **Monolith over microservices:** Appropriate for project scope and clarity.
* **Relational vs NoSQL:** Data access layer is designed to allow swapping between RDS and DynamoDB with minimal changes.

These decisions mirror real-world trade-offs commonly made in mid-sized engineering teams.

---

## Future Improvements

Potential extensions (not required for core goals):

* Authentication (API keys or JWT)
* Pagination and sorting for query endpoints
* Docker-based deployment
* Basic metrics and monitoring

---

## What This Project Demonstrates

* Backend service development in Java
* REST API design and persistence
* AWS fundamentals: compute, networking
* CI/CD automation
* Production-minded engineering without unnecessary complexity

---

## Author

**Mark Heydenrych**
Backend Software Engineer
