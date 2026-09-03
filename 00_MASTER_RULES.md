# REVIVE MESH — MASTER BUILD CONTRACT

## Mission
Build REVIVE MESH, an incident-aware, self-learning revenue recovery control plane for a synthetic payment environment.

Core thesis:
> The smartest recovery system isn't the one that retries the most. It's the one that knows exactly when to retry—and when not to.

## Three signature innovations — MUST be real
1. FAILURE SWARM
   Detect correlated/systemic payment failures and treat them as incidents instead of thousands of isolated failures.
2. RECOVERY BUDGET
   Treat recovery attempts/interventions as scarce resources and allocate them to opportunities with the highest expected net incremental recovery.
3. COUNTERFACTUAL RECOVERY
   Evaluate retry, delayed retry, alternate method, payment link/contact, and DO_NOTHING against a counterfactual baseline and choose the highest expected net incremental value.

## Required stack
- Java 21 + Spring Boot: core backend/business APIs
- PostgreSQL: durable source of truth
- Redis: fast ephemeral state/counters/locks/rate limits
- Apache Kafka: payment/recovery event backbone
- Python + FastAPI: ML/decision intelligence service
- Next.js + TypeScript: Recovery War Room
- Docker Compose: local reproducible environment
- LLM: bounded reasoning/explanation/tool-selection layer, NEVER the sole financial decision-maker

## Product scope
Synthetic/simulated payments only. Do not integrate with real money movement.

## Final deliverable
The final product MUST be deployed and accessible through a public HTTPS URL. Local Docker is for development/testing; it is not the final deliverable. See `14_CLOUD_DEPLOYMENT.md`.

## Required end-to-end flow
Payment simulator -> Kafka -> failure detection -> Failure Swarm -> recovery intelligence -> Counterfactual Recovery -> Recovery Budget -> policy/guardrails -> simulated action -> outcome -> audit -> learning/evaluation.

## Required actions
- RETRY_30M
- RETRY_TOMORROW
- PAYMENT_LINK
- ALTERNATE_METHOD
- CUSTOMER_CONTACT
- DO_NOTHING

## Required safety
- max retry attempts
- contact frequency limit
- merchant recovery budget
- high-value approval threshold
- incident-aware retry suppression
- deterministic policy engine
- audit every decision/action
- idempotent event processing
- LLM cannot bypass policy

## Required evaluation
Compare a fixed baseline recovery strategy against REVIVE MESH on the same generated evaluation data.

Report:
- recovered revenue
- incremental recovered revenue
- recovery rate
- number of retries
- customer contacts
- recovery/action cost
- avoided unnecessary actions
- Failure Swarm detection precision/recall or clearly defined detection metrics
- decision prediction vs actual outcome

Never invent metrics. Generate and display measured values.

## Engineering rules
- Prefer a modular monolith over unnecessary microservices.
- Keep boundaries clear so services can be separated later.
- Do not add technologies not listed above unless absolutely necessary.
- Do not create fake AI features.
- Do not use the LLM to calculate money or override deterministic policies.
- Use typed DTOs/schemas and validation.
- Design for idempotency and duplicate events.
- Keep configuration in environment variables.
- Use migrations for database schema.
- Add tests for critical business logic.
- Keep code readable and explainable.
- Do not refactor unrelated code during scoped tasks.

## Antigravity operating rules
Before changing code:
1. Inspect the existing repository and relevant files.
2. Read this master contract if available.
3. Read ONLY the current task prompt.
4. Produce a short implementation plan.
5. Implement only the requested scope.
6. Run the narrowest relevant tests first, then broader tests.
7. Fix failures caused by your changes.
8. Report files changed, tests run, and any remaining issue.
9. Do not rewrite working code just for style.
10. Do not install dependencies unless required.

Never ask the user to paste files that you can inspect locally.
Never generate large documentation unless requested.
Never regenerate existing code from scratch.
