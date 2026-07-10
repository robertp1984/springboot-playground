# SpringBoot Playground

A comprehensive multi-module Spring Boot application demonstrating modern microservices architecture, event-driven processing, and AI-powered categorization using cutting-edge technologies.

**Copyright (C) 2025-2026 Robert Piasecki**

## Overview

SpringBoot Playground is a multi-module Maven project that showcases a note-taking application with automatic categorization capabilities using keywords or AI (either OpenAI or AWS Bedrock). The project demonstrates real-world patterns including:

- **Event-Driven Architecture** using Apache Kafka
- **Microservices** communication via message streaming
- **AI-Powered Features** using OpenAI and AWS Bedrock
- **Stream Processing** with Kafka Streams
- **Multi-Database Support** (PostgreSQL, MongoDB, OpenSearch)
- **Docker Compose** orchestration for full-stack deployment

##  Project Structure

### Modules

#### 1. **springboot-note**
The main REST API service for note management. Provides endpoints to create, read, update, and delete sticky notes with tag support.

**Key Features:**
- REST API for note operations
- PostgreSQL database and JPA / Hibernate for persistent storage
- Kafka producer (both JSON and Avro formats)
- Aspect-oriented programming for monitoring and event recording
- Spring Security for authentication/authorization
- Flyway database migrations
- OpenSearch integration for search capabilities

#### 2. **springboot-note-categorizer**
A Kafka Streams application that consumes sticky notes from Kafka topics and automatically categorizes them using AI models.

**Key Features:**
- Kafka Streams topology for real-time categorization
- Support for multiple categorization engines:
  - **OpenAI Integration** via Spring AI
  - **AWS Bedrock Integration**
  - **Keyword-based Categorization** (fallback)
- Supported categories: Java, Spring, JPA, Kafka, Git, Docker, Cloud

#### 3. **springboot-note-common**
Shared library containing common data models and Avro schemas.

**Key Features:**
- Avro schema definitions for type-safe serialization
- Code generation from Avro schemas

##  Running

### Quick Start with Docker

The easiest way to run the entire stack:

```bash
# Build and start all services using Docker Compose
./build-and-run-docker.sh
```

This starts:
- **PostgreSQL** (port 5432): Database for notes
- **MongoDB** (port 27017): Optional document store
- **Kafka Cluster** (3 brokers): Message streaming
  - kafka1: 29092
  - kafka2: 29192
  - kafka3: 29392
- **Schema Registry** (port 8081): Avro schema management
- **Kafka Connect** (port 8083): Data integration to send categorized notes to OpenSearch
- **OpenSearch Cluster** (2 nodes): Full-text search
  - OpenSearch: 9200
  - Dashboards: 5601
- **Kafka REST Proxy** (port 8082): HTTP API for Kafka
- **springboot-note** (port 8080): REST API for the main application
- **springboot-note-categorizer**: Kafka Streams application to categorize notes and produce categorized output

Then the Python script tools/initial-data/create-sticky-notes-from-file.py can be used to send sample sticky notes via REST API for testing.

### Running Individually

#### Start springboot-note API:
```bash
cd springboot-note
mvn spring-boot:run
```
API available at: `http://localhost:8080`

#### Start categorizer:
```bash
cd springboot-note-categorizer
mvn spring-boot:run
```

## Kafka Integration

### Topics

**playground.stickynote.json**
- JSON-formatted sticky note events
- 9 partitions, replication factor 3
- Used for event streaming using JSON

**playground.stickynote.avro**
- Avro-serialized sticky note events
- Type-safe schema validation
- Used for event streaming using Avro and Schema Registry

**playground.stickynote.json.categorized**
- Categorized notes output from Kafka Streams
- Contains original note + assigned category

### Creating Kafka Topics

```bash
kafka-topics --bootstrap-server kafka1:29092 --create \
  --topic playground.stickynote.json \
  --partitions 9 --replication-factor 3

kafka-topics --bootstrap-server kafka1:29092 --create \
  --topic playground.stickynote.json.categorized \
  --partitions 9 --replication-factor 3
```

## API Endpoints

### Notes
- `GET /notes` - List all notes
- `GET /notes/{id}` - Get note by ID
- `POST /notes` - Create new note
- `PUT /notes/{id}` - Update note
- `DELETE /notes/{id}` - Delete note

### Tags
- `GET /tags` - List all tags
- `GET /tags/{id}` - Get tag by ID
- `POST /tags` - Create new tag
- `PUT /tags/{id}` - Update tag
- `DELETE /tags/{id}` - Delete tag


##  Configuration

### Environment Variables

**For springboot-note-categorizer:**
- `KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses
- `AWS_BEARER_TOKEN_BEDROCK`: AWS credentials for Bedrock
- `OPENAI_API_KEY`: OpenAI API key for ChatGPT access


## Security

- Spring Security enabled using basic authentication for REST API endpoints with credentials defined in the PostgreSQL database.
- OpenSearch database with authentication (username: admin, password: osPass@28g)
- PostgreSQL with SCRAM-SHA-256 authentication
- Default credentials are provided in compose.yaml (change in production)

### Method Timing

AOP is used to track method execution time (threshold: 5ms by default)

### OpenSearch Dashboard

The OpenSearch Dashboard is accessible at: `http://localhost:5601` (after authentication)

## Documentation

Additional documentation is available in `/docs`:
- `Create Kafka topics.md` - Kafka topic setup guide
- `Access Kafka topics.md` - Consumer commands
- `OpenSearch sample queries.md` - Search examples

