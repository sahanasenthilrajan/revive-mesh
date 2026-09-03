# REVIVE MESH — 6-DAY LIVE-PRODUCT EXECUTION ORDER

## Mission
The final deliverable is a LIVE, DEPLOYED, SUBMISSION-READY REVIVE MESH product.
Local Docker is the development/test environment, not the final destination.

## Operating rule
Complete one milestone -> test -> commit -> move forward.
Do not give Antigravity the entire prompt pack for every task.

## Day 1 — Foundation + deployment-ready configuration
Read:
- 00_MASTER_RULES.md
- 01_ARCHITECTURE.md
- 02_DATA_MODEL.md
- 14_CLOUD_DEPLOYMENT.md

Implement:
- repo structure
- Java 21 + Spring Boot
- Python/FastAPI skeleton
- Next.js skeleton
- PostgreSQL + Redis + Kafka Docker foundation
- migrations
- environment-variable configuration
- health/readiness endpoints
- CORS/configuration that can work locally and in cloud
- Dockerfiles or production build configuration
- no localhost hardcoding

Checkpoint:
All core components start locally and are configured for later cloud deployment.

## Day 2 — Kafka + Failure Swarm
Read:
- 03_EVENT_CONTRACTS.md
- 04_FAILURE_SWARM.md

Implement:
- event envelope
- Kafka topics
- producers/consumers
- idempotency
- payment failure flow
- Failure Swarm detector
- incident persistence
- retry suppression

Checkpoint:
Injected systemic incident -> live event flow -> swarm detected -> affected retries suppressed.

## Day 3 — Counterfactual Recovery
Read:
- 05_COUNTERFACTUAL.md

Implement:
- recovery prediction
- six candidate actions
- DO_NOTHING counterfactual
- incremental recovery probability
- expected net recovery value
- decision persistence
- API endpoints
- tests

Checkpoint:
Transaction detail shows all action values and a defensible recommendation.

## Day 4 — Recovery Budget + AI/Policy
Read:
- 06_RECOVERY_BUDGET.md
- 07_POLICY_AGENT.md

Implement:
- budget allocation
- concurrency/idempotency
- bounded AI reasoning
- policy engine
- guardrails
- high-value review
- audit ledger

Checkpoint:
AI/ML recommendation -> deterministic policy -> allow/block/review -> audit.

## Day 5 — Simulator + War Room + Evaluation
Read:
- 08_SIMULATOR_EVALUATION.md
- 09_FRONTEND_WAR_ROOM.md

Implement:
- reproducible synthetic simulator
- systemic incident scenarios
- baseline strategy
- REVIVE strategy
- evaluation metrics
- Recovery War Room
- decision ledger
- baseline vs REVIVE visualization

Checkpoint:
A judge can understand all three innovations and measured results from the UI.

## Day 6 — LIVE DEPLOYMENT + HARDENING
Read:
- 10_TESTING_DEPLOYMENT.md
- 14_CLOUD_DEPLOYMENT.md
- 13_FINAL_DEMO_CHECKLIST.md

Implement/deploy:
- production Docker images/builds
- cloud services
- managed PostgreSQL where practical
- Redis service
- Kafka service
- Spring Boot deployment
- Python intelligence deployment
- Next.js deployment
- environment variables/secrets
- HTTPS
- CORS
- health checks
- logs
- production smoke test

Then:
- fix critical bugs only
- run end-to-end test against deployed services
- verify dashboard against real deployed APIs
- verify evaluation reproducibility
- update README with live URL
- capture screenshots/video
- prepare 5-minute demo

## Priority
P0:
- live deployment
- Failure Swarm
- Counterfactual Recovery
- Recovery Budget
- Policy/guardrails
- Kafka
- evaluation
- War Room

P1:
- LLM explanation
- contextual bandit
- incident replay
- advanced charts

P2:
- advanced observability
- extra integrations
- nonessential infrastructure

If time collapses, cut P2 first. Never sacrifice P0.

## Final definition of done
A clean user can open the deployed URL, trigger/replay a synthetic payment scenario, see a Failure Swarm, inspect counterfactual recovery choices, see budget allocation, observe policy enforcement, inspect the audit trail, and compare measured REVIVE performance with the baseline.
