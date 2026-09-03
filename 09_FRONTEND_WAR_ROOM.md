# REVIVE MESH — RECOVERY WAR ROOM

## Goal
Build one polished dashboard, not many disconnected pages.

## Visual hierarchy
The first screen must immediately communicate:
- revenue at risk
- expected recovery
- recovered revenue
- active Failure Swarms
- recovery budget
- recovery actions
- baseline vs REVIVE

## Required sections

### 1. Executive metrics
Cards:
Revenue at Risk
Expected Recovery
Recovered Revenue
Active Incidents
Budget Remaining

### 2. Failure Swarm panel
Show:
- incident fingerprint
- current failure rate
- baseline rate
- confidence
- affected processor/issuer/region/method
- number of affected transactions
- current system action

### 3. Recovery queue
Columns:
- transaction
- amount
- failure
- recommended action
- expected net value
- confidence
- policy state

Sort by expected net value.

### 4. Counterfactual detail
When selecting a transaction:
show all six actions and the calculation breakdown.

### 5. Recovery Budget
Show:
total
allocated
consumed
remaining
top allocations

### 6. Decision Ledger
Show a chronological trace:
observation -> prediction -> counterfactual -> budget -> policy -> action -> outcome

### 7. Evaluation
Baseline vs REVIVE:
recovered revenue
recovery rate
actions
retries
contacts
cost
incremental recovery

## UX
- clean fintech-style interface
- responsive desktop layout
- meaningful empty/loading/error states
- no fake data once backend is connected
- use mock data only during isolated frontend development

## Demo requirement
A judge should understand the three innovations within 60 seconds of seeing the dashboard.
