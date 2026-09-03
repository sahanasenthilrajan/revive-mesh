# REVIVE MESH — DATA MODEL FREEZE

## PostgreSQL tables

### merchants
- id UUID PK
- name
- recovery_budget_amount
- max_retry_attempts
- max_contacts_per_day
- high_value_threshold
- created_at

### customers
- id UUID PK
- merchant_id FK
- historical_success_rate
- preferred_payment_method
- contactability_score
- created_at

### transactions
- id UUID PK
- merchant_id FK
- customer_id FK
- amount
- currency
- payment_method
- processor
- issuer
- region
- status
- created_at

### payment_attempts
- id UUID PK
- transaction_id FK
- attempt_number
- failure_code nullable
- latency_ms nullable
- status
- created_at

### recovery_cases
- id UUID PK
- transaction_id FK
- state
- eligible
- created_at
- updated_at

### recovery_decisions
- id UUID PK
- recovery_case_id FK
- recommended_action
- expected_net_value
- confidence
- model_version
- decision_reason
- created_at

### counterfactual_evaluations
- id UUID PK
- recovery_decision_id FK
- action
- recovery_probability
- no_action_probability
- incremental_probability
- expected_incremental_revenue
- action_cost
- friction_cost
- expected_net_value
- created_at

### recovery_actions
- id UUID PK
- recovery_case_id FK
- action
- status
- blocked_reason nullable
- executed_at nullable

### recovery_outcomes
- id UUID PK
- recovery_action_id FK
- recovered
- recovered_amount
- outcome_type
- observed_at

### failure_swarms
- id UUID PK
- fingerprint
- start_time
- end_time nullable
- affected_events
- failure_rate
- baseline_failure_rate
- confidence
- status
- suppression_enabled
- created_at

### recovery_budgets
- id UUID PK
- merchant_id FK
- period_start
- period_end
- allocated_amount
- consumed_amount
- remaining_amount

### policies
- id UUID PK
- merchant_id FK nullable
- policy_name
- policy_value
- enabled

### audit_events
- id UUID PK
- entity_type
- entity_id
- event_type
- actor
- reason
- metadata JSONB
- created_at

### model_predictions
- id UUID PK
- transaction_id FK
- model_version
- action
- predicted_probability
- actual_outcome nullable
- created_at

## Constraints
- transaction amount must be positive
- attempt_number >= 1
- action must be one of the frozen actions
- recovery action must be idempotent
- audit records are append-only
- unique event IDs must prevent duplicate event processing

Use Flyway or another migration mechanism already selected by the project. Do not introduce multiple migration systems.

## Redis keys
Examples:
- revive:merchant:{id}:budget
- revive:customer:{id}:daily_contacts
- revive:transaction:{id}:retry_count
- revive:incident:{fingerprint}:status
- revive:event:{eventId}:processed

Use TTL where appropriate.
