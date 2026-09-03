# REVIVE MESH — COUNTERFACTUAL RECOVERY

## Goal
For every eligible failed payment, compare interventions against DO_NOTHING and select the action with the highest expected net incremental value.

## Frozen actions
- RETRY_30M
- RETRY_TOMORROW
- PAYMENT_LINK
- ALTERNATE_METHOD
- CUSTOMER_CONTACT
- DO_NOTHING

## Required model output
For each action:
P(recovery | action, context)

For DO_NOTHING:
P(recovery | no action, context)

## Core calculation
incremental_probability(action)
= P(recovery | action, context)
  - P(recovery | DO_NOTHING, context)

expected_incremental_revenue
= incremental_probability * transaction_amount

expected_net_value
= expected_incremental_revenue
  - action_cost
  - friction_cost
  - operational_cost

Select argmax(expected_net_value), subject to policy constraints.

## Context features
Use available features such as:
- amount
- payment_method
- processor
- issuer
- region
- failure_code
- attempt_number
- customer historical success
- time features
- incident state
- customer contact history

## MVP modeling
Start with a simple interpretable supervised model or calibrated probability model. If action-specific historical labels are insufficient, use a carefully documented synthetic data-generating process and clearly label the experiment as simulated.

Do NOT fabricate causal claims.
Call this an "estimated incremental recovery" unless the experimental design actually supports causal inference.

## Required UI
For a transaction, display a comparison table:
Action | recovery probability | incremental probability | expected incremental revenue | cost | expected net value

Always show DO_NOTHING.

## Test cases
- highest-value action selected
- DO_NOTHING selected when all interventions have negative/low net value
- policy can override recommendation
- incident state changes candidate ranking
- deterministic calculation given fixed predictions
