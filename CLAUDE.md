# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

`kb-server` ("Kafka Broker Module") is a Spring Boot 2.4 / Java 11 service that bridges NLMK's Kafka topics (Avro-encoded messages from CCM, PDM, SAP, MES, and Zifra/NSI systems) to a "product attestation" REST backend (`product-api`), and forwards attestation/verification results back out over Kafka and REST. It persists processed messages and configuration in PostgreSQL.

## Git & PR conventions

- Write commit messages, PR descriptions, and code review comments in Russian. Do not translate programming/technical terminology (keep terms like `commit`, `PR`, `merge`, `listener`, `endpoint` etc. as-is).
- Do not add any Claude/AI attribution to commits or PRs — no `Co-Authored-By: Claude` trailer, no "Generated with Claude Code" footer, no mention of Claude/AI authorship anywhere in the commit or PR. Commits and PRs must look like they were authored solely by the user's own GitHub account.

## Build & run

The project depends on an external library `product-api` (group `com.nlmk.attestation.product.api`) that is **not on Maven Central**. Before building, either:
1. Clone and `mvn install` https://git.nlmk.com/apcs/product-api locally (preferred), or
2. Configure the `nlmk-apcs-central` repository credentials via environment variables referenced in `settings.xml`.

```bash
# Full build (runs checkstyle + tests + jacoco)
mvn clean package

# Generate Java classes from AVRO schemas in src/main/resources/avro
# (also runs automatically at the generate-sources phase)
mvn generate-sources

# Run the server against target/kb-server.jar
java -jar target/kb-server.jar

# Run in dev mode with auto-restart on rebuild (spring-boot-devtools style)
mvn clean spring-boot:run

# Run a single test class
mvn test -Dtest=CcmPtsKafkaServiceTest

# Run a single test method
mvn test -Dtest=CcmPtsKafkaServiceTest#someMethodName

# Run checkstyle only (also runs automatically at the validate phase, build fails on violations)
mvn checkstyle:check
```

- Checkstyle config: `checkstyle.xml` (rules), `checkstyle-suppressions.xml` (exceptions). Max line length is 180.
- AVRO schema sources live in `src/main/resources/avro/*.avsc`; generated Java lands under `src/main/java/nlmk/...` and `src/main/java/com/nlmk/s3/...` — these packages are generated code, not hand-written, and shouldn't be edited directly (edit the `.avsc` and regenerate instead).
- Default local port is `8089` (`server.port` in `application.properties`).
- Spring profiles: `dev` (local sandbox, Kafka pointed at a local broker) and `prod` (deployed in-container, uses `application-prod.properties`, see `Dockerfile`). Tests use `application-test.properties`.
- Flyway manages the DB schema; migrations are in `src/main/resources/db/migration`. Seed data is split by profile under `db/seeds-dev`, `db/seeds-prod`, `db/seeds-psql`, `db/seeds-test`. Test runs additionally apply `classpath:db/seeds-psql,classpath:db/seeds-test`.
- Tests use an embedded H2 database and `spring-kafka-test` (embedded Kafka) — no external services are required to run `mvn test`.
- Swagger UI is served at `http://127.0.0.1:<port>/swagger-ui.html` once the server is running; it requires OAuth2 (Keycloak) authorization via the `Authorize` button using the test realm client credentials described in `README.md`.
- Jaeger tracing (optional, local): run a `jaegertracing/all-in-one` container and point `JAEGER_HOST`/`JAEGER_PORT` at it; UI at `http://localhost:16686`.

## Architecture

### Message flow

The service has two symmetric halves:

1. **Inbound (Kafka → REST)**: `@KafkaListener`s in `service/listener/*KafkaService.java` (one per source system/topic family: CCM PDS/PGP/PTS/KC1/KC2, PDM, SAP, Zifra) receive Avro-deserialized Kafka messages, adapt them into a common request shape via a `*MessageAdapter`/`*RequestAdapter` (see `service/ccm/**`), and post them to the `product-api` attestation backend through `CcmCommonService` / `WebClient` (`config/WebClientConfig.java`). Listener methods manually ack/nack (`ContainerProperties.AckMode.MANUAL_IMMEDIATE`): a business exception typically acks (skips) the message and rethrows for logging, while a transient/remote-service exception nacks with a configurable delay (`kafka.ack.nack.sleep-time`) to trigger a redelivery/retry.
2. **Outbound (result → Kafka/REST)**: `service/result/sending/*` converts attestation results back into per-system Avro/REST payloads via `*ResultAdapter` implementations (Pts, Kc1, Kc2, Mes, Pgp, Phpp) and `service/sender/*` (`PamSender`, `PsmSender`, `PgpSender`) pushes them onward — either to a Kafka REST proxy or to a downstream HTTP service.

### Per-source-system package triads

Most source systems (`pds`, `pts`, `pgp`, `kc/kc1`, `kc/kc2`, `phpp`) follow the same three-file pattern under `service/ccm/<system>/`:
- `*KafkaRequestAdapterImpl` — maps the raw Avro Kafka message into a REST request DTO.
- `*RestRequestAdapterImpl` / `*RestResponseAdapterImpl` — maps to/from the `product-api` REST contract.
- `*MessageAdapterImpl` — orchestrates the above into the common `CcmMessageAdapter` interface used by the listener.

When adding a new source system or topic, mirror this triad rather than special-casing the listener.

### Kafka consumer configuration

Each source system has its own `config/broker/*BrokerConfig.java` + `*ConsumerProperties.java` pair, all extending `BrokerConfigBase` (`config/broker/BrokerConfigBase.java`), which builds a `ConcurrentKafkaListenerContainerFactory` using `KafkaAvroDeserializer` (Confluent schema registry) wrapped in Spring's `ErrorHandlingDeserializer`, with optional mutual TLS (`ssl-enabled` + truststore/keystore paths). Each listener is independently toggleable via `kafka.<system>.enable` (`@ConditionalOnProperty`, defaults to enabled except `kafka.ccm.pts.enable=false`).

### Security

- Inbound REST API is secured via Keycloak (`WebSecurityConfig`, `keycloak-spring-boot-starter`), bearer-only JWT. Per-endpoint role requirements are declared declaratively in `application*.properties` under `keycloak.securityConstraints[n]` (role → HTTP method(s) → URL pattern), not in Java annotations — check there first when working on access control for an endpoint.
- Outbound calls to other internal services use OAuth2 client-credentials (`spring.security.oauth2.client.registration.*`), with two separate client registrations: `keycloak` (default) and `mes-rolling` (for MES), selected in `WebClientConfig`.

### Controllers

Controller interfaces (e.g. `KbController`, `PdmReimportController`, `MdmReimportController`, `DictionaryConfigController`, `ResultConfigController`, `CaptureDataChangeController`) are separated from their `*Impl` classes — the interface typically carries the Swagger/OpenAPI (`springdoc`) annotations, keeping the implementation classes focused on logic. Follow this split for new endpoints.

### Reimport

`PdmReimportController`/`MdmReimportController` expose manual reimport/reprocessing endpoints (roles `reimport`, `process-pdm-message`, `delete-pdm-message`) used to replay or fix previously ingested messages — see recent commit history around "fix реимпорта записей" for context on prior bugs in this area.

### Exceptions

Custom exceptions live in `exception/` and are deliberately fine-grained (`AttestationResultException`, `AttestationResultSenderException`, `KafkaMessageProcessingException`, `KafkaRestConfigException`, `RemoteServiceSenderException`, `DateTimeParseException`, etc.) because the Kafka listeners branch their ack/nack behavior per exception type — check `service/listener/*KafkaService.java` for the current retry semantics before adding a new exception type or changing an existing one, and update all `*KafkaService` listeners consistently since the catch blocks are duplicated per listener rather than shared.

### S3

`config/S3ClientConfig.java` configures Minio S3 clients; `com.nlmk.s3.proxy.s3notification` is an Avro-generated notification type used by the SAP Kafka flow (`kafka.sap.topic.s3.*`) for S3 event-triggered ingestion.
