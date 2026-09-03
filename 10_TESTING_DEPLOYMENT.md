# REVIVE MESH — TESTING + DEPLOYMENT

## Testing priorities

### Backend unit tests
- policy rules
- counterfactual value calculation
- budget allocation
- recovery eligibility
- incident suppression
- idempotency

### Integration tests
- PostgreSQL persistence
- Kafka producer/consumer flow
- Redis state
- Spring Boot -> Python intelligence API

### Intelligence tests
- feature transformation
- deterministic calculations
- model input/output schema
- evaluation split integrity
- edge cases

### End-to-end smoke test
Given a generated payment incident:
1. payment failures enter Kafka
2. swarm is detected
3. affected retries are suppressed
4. eligible transactions receive counterfactual scores
5. budget allocates actions
6. policy validates actions
7. outcomes are recorded
8. dashboard reflects results
9. audit ledger contains the full decision path

## Docker
docker compose should start:
- postgres
- redis
- kafka
- backend
- intelligence
- frontend

Use health checks where practical.

## Configuration
Use .env.example.
Never commit secrets.

## CI
If time permits:
- build
- unit tests
- integration tests
- frontend lint/build

Do not spend buildathon time on elaborate CI infrastructure.

## Definition of done
A clean checkout can start the project using documented commands and execute the main demo flow.
