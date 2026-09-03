# REVIVE MESH — CLOUD DEPLOYMENT FREEZE

## Objective
REVIVE MESH must finish as a live web product with a public HTTPS URL.
Local Docker Compose is for development and integration testing only.

## Deployment principle
Prefer the simplest reliable cloud architecture that can host:
- Next.js frontend
- Spring Boot backend
- Python/FastAPI intelligence service
- PostgreSQL
- Redis
- Kafka

Do not introduce Kubernetes unless it is already required by the selected platform. A buildathon project should optimize for reliability and speed.

## Required production topology

Internet
  -> HTTPS
  -> Next.js frontend
  -> Spring Boot API
       -> PostgreSQL
       -> Redis
       -> Kafka
       -> Python/FastAPI intelligence service

Kafka carries event-driven workflows.
Spring Boot remains authoritative for business state and policy enforcement.
Python provides ML/decision intelligence.
Next.js is the user interface.

## Cloud selection rule
Before implementation, inspect current pricing/free-tier/availability for candidate services.
Prefer services that:
- can be deployed quickly
- provide HTTPS
- support environment variables/secrets
- have predictable limits
- are practical for a student/buildathon project

The implementation agent must not invent current provider pricing. If live web research is available, verify current limits before choosing a provider.

## Recommended deployment strategy
Use managed services where they save operational time:
- managed PostgreSQL
- managed Redis
- managed Kafka or a hosted Kafka-compatible service
- container hosting for Spring Boot and Python
- managed/static hosting for Next.js

If a single platform can reliably host multiple containers and required managed services, prefer simplicity.

## Production configuration
Never hard-code:
- localhost
- database credentials
- Kafka credentials
- Redis credentials
- LLM API keys
- service URLs

Use environment variables.

Required conceptual variables:
- DATABASE_URL / DB_HOST etc.
- REDIS_URL
- KAFKA_BOOTSTRAP_SERVERS
- KAFKA_SECURITY settings if required
- INTELLIGENCE_SERVICE_URL
- LLM_API_KEY
- CORS_ALLOWED_ORIGINS
- APP_ENV
- model/config versions

Do not commit .env files containing secrets.
Provide .env.example with placeholders.

## Networking
- HTTPS for public traffic
- frontend calls backend using configured public API URL
- backend calls intelligence service through private/internal URL when supported
- restrict database/Redis/Kafka access to application services where possible
- configure CORS to the actual frontend origin, not '*', in production unless the platform forces it and the risk is understood

## Health checks
Spring Boot:
- liveness
- readiness

Python:
- health endpoint

Frontend:
- production build must succeed

Container health checks should verify dependencies where practical.

## Reliability
Implement:
- graceful startup failure when required configuration is missing
- Kafka reconnect behavior
- idempotent event processing
- database migration on deployment or a documented migration step
- safe degraded mode if the LLM is unavailable

## Deployment order
1. Create managed PostgreSQL.
2. Create Redis.
3. Create Kafka.
4. Deploy intelligence service.
5. Deploy Spring Boot backend.
6. Run migrations.
7. Verify backend health.
8. Deploy Next.js.
9. Configure frontend API URL.
10. Run end-to-end smoke test.
11. Verify evaluation/demo scenario.
12. Record final live URL.

## Production smoke test
From the public UI:
1. load War Room
2. generate/replay payment data
3. create systemic incident
4. observe Failure Swarm
5. inspect a failed transaction
6. view six counterfactual actions
7. verify Recovery Budget
8. trigger blocked policy case
9. inspect audit ledger
10. view baseline vs REVIVE metrics

## Cost control
Because the deadline is less than one week:
- use free/low-cost tiers where realistically available
- shut down unused resources after the submission if necessary
- do not create redundant environments
- do not use managed infrastructure that is vastly more complex than needed

## Deployment security
- secrets only in provider secret/env configuration
- least-privilege credentials
- no secrets in Git
- no real customer/payment data
- synthetic payment data only
- never expose database/Redis/Kafka publicly unless unavoidable and explicitly secured

## README must contain
- live application URL
- architecture diagram
- local development instructions
- deployment overview
- environment variable list without secret values
- demo steps
- evaluation methodology
- limitations

## Definition of done
The deployed application is reachable over HTTPS and the complete demo flow works against deployed services, not local mocks.
