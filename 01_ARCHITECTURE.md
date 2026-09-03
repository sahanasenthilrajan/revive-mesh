# REVIVE MESH — ARCHITECTURE FREEZE

## Goal
Implement a buildathon-ready modular monolith with separate intelligence service and frontend.

## Logical components

### Backend: Spring Boot
Modules/packages:
- payment
- recovery
- incident
- budget
- policy
- audit
- evaluation
- common

Responsibilities:
- REST APIs
- business state
- orchestration
- Kafka producers/consumers
- PostgreSQL persistence
- Redis state
- deterministic policy enforcement

### Intelligence: Python/FastAPI
Modules:
- features
- prediction
- counterfactual
- swarm
- bandit
- schemas
- evaluation

Responsibilities:
- feature transformation
- recovery probability estimation
- counterfactual estimates
- Failure Swarm scoring
- contextual learning/bandit experiment
- model evaluation

### Frontend: Next.js
Main product:
RECOVERY WAR ROOM

Views/panels:
1. executive metrics
2. active Failure Swarms/incidents
3. recovery priority queue
4. transaction decision detail
5. counterfactual simulator
6. recovery budget
7. audit/decision ledger
8. baseline vs REVIVE comparison

## Event flow
payment.created
payment.failed
failure.swarm.detected
recovery.decision.created
recovery.action.requested
recovery.action.executed
recovery.outcome.recorded
policy.action.blocked
incident.updated
audit.event.created

## Data flow
Simulator emits payment events.
Kafka distributes events.
Spring Boot owns transactional state.
Python scores intelligence.
Spring Boot remains authoritative for final policy enforcement.
Frontend reads backend APIs and live/near-live state.

## Decision authority
Python may recommend.
LLM may reason/explain/select among allowed tools.
Spring Boot policy engine is authoritative.
No model or LLM can execute an action directly without policy approval.

## Non-goals
- real payment processing
- real customer messaging
- real financial transactions
- Kubernetes
- complex microservice deployment
- production-grade distributed training

## Definition of done
A user can start Docker Compose, generate payment traffic, see a systemic incident detected, see retries suppressed, inspect counterfactual options, see budget allocation, observe a policy block, inspect the decision ledger, and compare REVIVE against the baseline.
