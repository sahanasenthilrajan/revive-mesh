# REVIVE MESH — SIMULATOR + EVALUATION

## Goal
Create reproducible synthetic data that supports the product demo and honest evaluation.

## Simulator requirements
Generate:
- merchants
- customers
- transactions
- payment attempts
- successful payments
- random failures
- recoverable failures
- systemic incidents

## Features
At minimum:
transactionId
merchantId
customerId
amount
paymentMethod
processor
issuer
region
failureCode
attemptNumber
timestamp

## Failure scenarios
Include:
1. random transient declines
2. repeated customer-level failures
3. processor incident
4. issuer incident
5. regional incident
6. payment-method-specific incident
7. failures that naturally recover without intervention

This last scenario is important because it makes DO_NOTHING meaningful.

## Reproducibility
Allow a random seed.
Allow scenario configuration.
Document generated distributions.

## Baseline
Implement a deliberately simple fixed strategy:
- retry eligible failures after a fixed delay
- retry up to a fixed maximum
- no swarm detection
- no budget optimization
- no counterfactual ranking

Do not make the baseline intentionally absurd; it should represent a plausible simple recovery policy.

## REVIVE evaluation
Run both strategies on the same evaluation population with the same outcome-generation logic.

## Metrics
Primary:
- total recovered revenue
- recovery rate
- incremental recovered revenue vs baseline

Secondary:
- number of actions
- retry count
- customer contacts
- action cost
- unnecessary actions avoided
- systemic incident detection metrics
- predicted vs actual recovery

## Important
Never hard-code final performance numbers.
Never compare on different random populations.
Never train on the evaluation outcome labels.
Clearly separate training/simulation data from evaluation data.

## Output
Generate machine-readable results and a JSON/CSV summary used by the dashboard.

## Demo
Provide a command or endpoint to:
1. reset simulation
2. generate baseline data
3. generate REVIVE run
4. compute metrics
5. expose results to dashboard
