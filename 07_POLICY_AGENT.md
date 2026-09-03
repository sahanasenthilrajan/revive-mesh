# REVIVE MESH — AI AGENT + POLICY/SAFETY

## Principle
The AI/LLM is not the financial authority.

Architecture:
LLM/ML recommendation -> deterministic policy engine -> allow/block -> simulated execution -> audit.

## Allowed tools
The agent may call bounded tools such as:
- get_transaction_context
- get_incident_status
- simulate_recovery_options
- get_recovery_budget
- recommend_action
- request_recovery_action
- explain_decision

The final action request must go through policy validation.

## Agent responsibilities
Use the LLM for:
- summarizing incident context
- explaining why a recommendation makes sense
- selecting among already-defined bounded tools
- generating merchant-readable explanations

Do not use the LLM for:
- arithmetic involving authoritative money values
- bypassing policies
- inventing transaction state
- changing budgets
- directly executing payment operations

## Guardrails
At minimum:
1. max retry attempts
2. max customer contacts/day
3. active systemic incident suppression
4. merchant recovery budget
5. high-value human approval requirement
6. action allowlist
7. idempotency
8. audit logging

## Example
Model says RETRY_30M.
Policy checks:
attempt_number = 3
max_retries = 3

Result:
BLOCKED
Reason:
MAX_RETRY_ATTEMPTS_REACHED

## Audit decision ledger
For every decision store:
- observed context
- candidate actions
- predictions
- counterfactual values
- budget state
- policy result
- final action
- model version
- actual outcome later

## Human escalation
For high-value cases above merchant threshold, route to NEEDS_REVIEW rather than automatic execution.

## Failure behavior
If the LLM service is unavailable:
- deterministic recovery engine continues where possible
- no unsafe fallback
- audit the degraded mode
