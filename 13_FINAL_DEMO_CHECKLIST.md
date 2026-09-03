# REVIVE MESH — FINAL DEMO CHECKLIST

## 5-minute story

### 0:00 Problem
Payment failures are not independent. Blind retries waste resources and create friction.

### 0:30 Failure Swarm
Inject systemic processor failure.
Show:
normal 2% -> elevated failure rate.
REVIVE detects one incident and suppresses unnecessary retries.

### 1:30 Counterfactual Recovery
Open failed transaction.
Show:
RETRY_30M
RETRY_TOMORROW
PAYMENT_LINK
ALTERNATE_METHOD
CUSTOMER_CONTACT
DO_NOTHING

Show expected net values.
Explain why the winner beats doing nothing.

### 2:30 Recovery Budget
Show limited budget.
Show highest-value opportunities selected.
Explain that recovery resources are scarce.

### 3:15 Safety
Trigger a prohibited retry/high-value case.
Show:
BLOCKED or NEEDS_REVIEW.
Open decision ledger.

### 4:00 Learning
Show prediction vs actual outcome.
Show model/bandit evaluation or updated action performance.
Do not claim online self-learning unless the implemented loop truly updates.

### 4:30 Results
Show baseline vs REVIVE with measured metrics.

## Final line
"We didn't build an AI that retries harder. We built a recovery control plane that knows when recovery is worth pursuing."

## Before submission
- no secrets in repository
- README works
- Docker Compose works
- tests pass
- demo dataset reproducible
- evaluation metrics reproducible
- no fabricated numbers
- screenshots updated
- architecture diagram matches actual code
- three innovations clearly visible in code and UI
