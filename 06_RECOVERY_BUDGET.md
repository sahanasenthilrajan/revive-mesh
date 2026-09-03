# REVIVE MESH — RECOVERY BUDGET

## Goal
Allocate limited recovery resources to maximize expected net incremental recovery.

## Budget types
For MVP, use a merchant-level daily recovery action budget.

Example:
merchant has 500 intervention units/day.

## Candidate score
Use:
priority_score = expected_net_value

Optionally normalize by action unit cost:
efficiency_score = expected_net_value / max(action_resource_cost, epsilon)

Keep the primary explanation simple.

## Allocation
1. collect eligible recovery candidates
2. remove policy-ineligible candidates
3. remove incident-suppressed actions
4. calculate score
5. rank descending
6. allocate until budget is exhausted
7. persist allocation
8. emit events

## Required behavior
- budget must be atomic/consistent
- no negative remaining budget
- duplicate event cannot consume budget twice
- blocked actions must not consume execution budget
- show allocated/consumed/remaining values

## Demo scenario
Create more eligible recovery opportunities than the budget allows.
Show that REVIVE selects higher-value opportunities instead of blindly retrying all failures.

## Required UI
Budget card:
- total budget
- reserved
- consumed
- remaining
- top allocated opportunities

## Tests
- allocation respects limit
- concurrent allocations do not overspend
- duplicate allocation is idempotent
- zero/negative value candidates are deprioritized or rejected
