# REVIVE MESH — FAILURE SWARM IMPLEMENTATION

## Goal
Detect when many payment failures are actually one systemic incident.

## MVP algorithm
Use a transparent two-stage approach.

### Stage 1: anomaly score
For a rolling time window:
failure_rate = failures / payment_attempts

Compare current rate against a historical or synthetic baseline.

Create an anomaly score from:
- rate increase
- absolute failure rate
- persistence across consecutive windows

### Stage 2: correlated fingerprint
Group failures using:
- processor
- issuer
- payment_method
- region
- failure_code
- time bucket

A cluster becomes a Failure Swarm candidate when:
- minimum event count is reached
- failure rate exceeds configured threshold
- anomaly score exceeds threshold
- concentration on one or more dimensions is high

## Output
Create failure_swarm record and emit FailureSwarmDetectedEvent.

Example UI explanation:
"18.7% failure rate vs 2.1% baseline; 91% of failures concentrated on PSP_A + CARD + BANK_X."

## Incident behavior
When a swarm is active:
- suppress retry actions for affected transactions when policy says retry is likely wasteful
- continue evaluating eligible alternate recovery actions
- resume normal recovery when incident closes

## Important
Do not claim the algorithm predicts root cause. It detects correlated systemic failure patterns.
Do not use an opaque LLM for swarm detection.

## Test cases
1. normal traffic -> no swarm
2. single random failure -> no swarm
3. sustained processor-specific spike -> swarm
4. spike below threshold -> no swarm
5. duplicate events -> no inflated cluster
6. incident closes -> suppression lifted
