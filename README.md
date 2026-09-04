# REVIVE MESH - Incident-Aware Revenue Recovery Platform

[![GitHub](https://img.shields.io/badge/GitHub-sahanasenthilrajan%2Frevive--mesh-blue)](https://github.com/sahanasenthilrajan/revive-mesh)

## Live Demo
🚀 **Status**: Local deployment verified - Cloud deployment requires manual platform setup (see [DEPLOYMENT.md](./DEPLOYMENT.md))

## What is REVIVE MESH?

REVIVE MESH is a revenue recovery control plane that uses counterfactual reasoning and incident detection to recover lost revenue from payment failures. Instead of treating every payment failure the same, REVIVE MESH detects systemic incidents, evaluates six recovery strategies per transaction, and enforces budget constraints to maximize recovery ROI.

### Core Innovation: Counterfactual Recovery Engine

For every failed payment transaction, the system evaluates six parallel counterfactual scenarios:
1. **DO_NOTHING** - Let it naturally retry
2. **AUTO_RETRY** - Immediate retry with same method
3. **PAYMENT_METHOD_PROMPT** - Ask customer to switch card
4. **CUSTOMER_CONTACT** - Proactive phone/email outreach
5. **DISCOUNT_INCENTIVE** - Offer 5-10% discount to complete payment
6. **MANUAL_REVIEW** - Route to human specialist

Each counterfactual provides:
- **Predicted recovery probability** (e.g., 23% → 67%)
- **Recovery cost** (e.g., $0 → $12.50)
- **Expected revenue** calculation
- **Net gain** after costs

The system then applies budget constraints and policy guardrails before recommending the optimal action.

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                         REVIVE MESH                          │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌───────────────┐      ┌──────────────────┐               │
│  │   Next.js     │─────▶│   Spring Boot    │               │
│  │   War Room    │      │   Backend API    │               │
│  │  (Frontend)   │◀─────│  (Control Plane) │               │
│  └───────────────┘      └────────┬─────────┘               │
│                                   │                          │
│         ┌─────────────────────────┼───────────────────┐     │
│         │                         │                   │     │
│         ▼                         ▼                   ▼     │
│  ┌─────────────┐         ┌──────────────┐    ┌──────────┐ │
│  │ PostgreSQL  │         │    Redis     │    │  Kafka   │ │
│  │  (State)    │         │   (Cache)    │    │ (Events) │ │
│  └─────────────┘         └──────────────┘    └────┬─────┘ │
│                                                     │       │
│                                              ┌──────▼─────┐ │
│                                              │  Python    │ │
│                                              │Intelligence│ │
│                                              │  Service   │ │
│                                              └────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

**Technology Stack:**
- **Frontend**: Next.js 16.3 (Turbopack), TypeScript, Tailwind CSS
- **Backend**: Spring Boot 3.3, Java 21, Maven
- **Intelligence**: Python 3.12, FastAPI, scikit-learn
- **Data**: PostgreSQL 15, Redis 7, Apache Kafka 3.7
- **Infrastructure**: Docker, Docker Compose

## Key Features

### 1. Failure Swarm Detection
Real-time detection of systemic payment incidents using spatiotemporal clustering:
- Groups failures by merchant_id, error_code, and payment_gateway
- Triggers when failure rate > 3× baseline in 60-second window
- Prevents wasted recovery spend on systemic outages

### 2. Counterfactual Recovery Engine
Evaluates all six recovery strategies per transaction:
- Calls Intelligence Service for ML-based probability predictions
- Calculates expected value: `P(recovery) × Revenue - Cost`
- Ranks by net gain
- Returns all six options to backend for policy enforcement

### 3. Recovery Budget Management
Finite budget allocation with greedy optimization:
- Budget pool: $500/day (configurable)
- Allocates to highest net-gain cases first
- Blocks recovery when budget exhausted
- Tracks spend vs. recovered revenue

### 4. Policy Guard
Rule-based constraints before execution:
- DO_NOTHING blocked during active swarm
- DISCOUNT_INCENTIVE blocked if recovery_probability < 40%
- Ensures actions align with business policies

### 5. War Room Dashboard
Real-time operations view showing:
- Active failure swarms
- Recovery budget remaining
- Pending recovery cases
- Policy-blocked cases
- Recovered revenue

### 6. Demo Scenarios
Five pre-built scenarios for evaluation:
- **Scenario 1**: Normal failures (isolated, no swarm)
- **Scenario 2**: Failure swarm (15 failures in 60s trigger incident)
- **Scenario 3**: Budget competition (10 cases compete for $500 budget)
- **Scenario 4**: DO_NOTHING wins (highest net gain despite low action probability)
- **Scenario 5**: Intervention wins (high-cost action justified by revenue)

## Local Development

### Prerequisites
- Docker & Docker Compose
- Java 21+ (for local backend development)
- Node 20+ (for local frontend development)
- Python 3.12+ (for local intelligence development)

### Quick Start
```bash
# Clone repository
git clone https://github.com/sahanasenthilrajan/revive-mesh.git
cd revive-mesh

# Start all services
docker compose up -d

# Wait for services to be healthy (~30 seconds)
docker compose ps

# Verify services
curl http://localhost:8080/actuator/health  # Backend
curl http://localhost:8000/health           # Intelligence
curl http://localhost:3000                  # Frontend
```

### Access Points
- **Frontend**: http://localhost:3000
- **Backend API**: http://localhost:8080
- **Intelligence API**: http://localhost:8000
- **PostgreSQL**: localhost:5432 (user: revive_user, db: revive_mesh)
- **Redis**: localhost:6379
- **Kafka**: localhost:9092

### Run Demo Scenario
```bash
# Trigger Failure Swarm scenario
curl -X POST http://localhost:8080/api/simulator/scenario/2

# Check logs for recovery processing
docker logs revive-backend --tail 50

# View War Room
open http://localhost:3000
```

## Event Flow

```
Simulator
    │
    ├──▶ Kafka Topic: payment-failures
    │         │
    │         ├──▶ PaymentFailureConsumer (swarm-detection-group)
    │         │         │
    │         │         └──▶ FailureSwarmService
    │         │                   └──▶ Incident detection
    │         │
    │         └──▶ PaymentFailureRecoveryConsumer (recovery-engine)
    │                   │
    │                   └──▶ RecoveryDecisionService
    │                           │
    │                           ├──▶ Intelligence Service (counterfactuals)
    │                           ├──▶ RecoveryBudgetService (allocation)
    │                           ├──▶ PolicyGuard (constraints)
    │                           └──▶ PostgreSQL (persist decision)
```

## Database Schema

Key tables:
- **recovery_cases**: One per failed transaction
- **recovery_decisions**: Stores all six counterfactuals + selected action
- **failure_swarm_incidents**: Detected systemic incidents
- **recovery_budget_tracking**: Budget allocation log

Migrations managed by Flyway (auto-run on startup).

## API Endpoints

### Simulator
- `POST /api/simulator/scenario/1` - Normal failures
- `POST /api/simulator/scenario/2` - Failure swarm
- `POST /api/simulator/scenario/3` - Budget competition
- `POST /api/simulator/scenario/4` - DO_NOTHING wins
- `POST /api/simulator/scenario/5` - Intervention wins

### Recovery
- `POST /api/recovery/cases` - Create recovery case
- `POST /api/recovery/cases/{caseId}/evaluate` - Trigger counterfactual evaluation
- `GET /api/recovery/decisions/{decisionId}` - Get decision details
- `GET /api/recovery/cases/{caseId}/decisions` - Get all counterfactuals for case

### Swarm Detection
- `GET /api/swarm/active` - List active failure swarm incidents

### Health
- `GET /actuator/health` - Backend health (includes DB, Redis)
- `GET /health` - Intelligence service health

## Testing

### Backend Tests
```bash
cd backend
./mvnw test
```

Note: Integration tests require running PostgreSQL, Redis, and Kafka. Use Docker Compose for test environment.

### E2E Test Flow
1. Start all services: `docker compose up -d`
2. Trigger scenario: `curl -X POST http://localhost:8080/api/simulator/scenario/2`
3. Verify Kafka consumption in logs: `docker logs revive-backend --tail 50`
4. Check database: `docker exec revive-postgres psql -U revive_user -d revive_mesh -c "SELECT COUNT(*) FROM recovery_cases;"`
5. Verify frontend updates: http://localhost:3000

## Deployment

See [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed cloud deployment instructions.

**Platform Options:**
- Railway (recommended for simplicity)
- Render (good free tier)
- Fly.io (good for containerized apps)

**Challenges:**
- Free tiers rarely include managed Kafka
- Consider Upstash Kafka or database-backed event queue for free deployments

## Limitations & Future Work

### Current Limitations
- Intelligence Service uses mock ML model (scikit-learn placeholder)
- No real-time LLM integration (would require API key + cost management)
- Kafka required for event-driven flow (adds deployment complexity)
- Single-region deployment only
- No authentication/authorization

### Future Enhancements
- Real ML model training on historical payment data
- LLM-powered recovery message generation
- Multi-tenant support with per-tenant budgets
- Real payment gateway integrations
- A/B testing framework for recovery strategies
- Advanced budget allocation algorithms (not just greedy)

## License
MIT

## Author
Sahana Senthilrajan
- GitHub: [@sahanasenthilrajan](https://github.com/sahanasenthilrajan)

## Acknowledgments
Built as a demonstration of incident-aware revenue recovery using counterfactual reasoning and policy-constrained optimization.

---

**Repository**: https://github.com/sahanasenthilrajan/revive-mesh  
**Status**: Local deployment verified ✅ | Cloud deployment pending manual setup ⏳
