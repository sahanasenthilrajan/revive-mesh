-- Recovery Cases, Decisions, Counterfactual Evaluations, Actions, Outcomes

CREATE TABLE recovery_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID NOT NULL,
    state VARCHAR(50) NOT NULL,
    eligible BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recovery_cases_transaction ON recovery_cases(transaction_id);
CREATE INDEX idx_recovery_cases_state ON recovery_cases(state);
CREATE INDEX idx_recovery_cases_eligible ON recovery_cases(eligible);

CREATE TABLE recovery_decisions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recovery_case_id UUID NOT NULL REFERENCES recovery_cases(id),
    recommended_action VARCHAR(50) NOT NULL,
    expected_net_value DECIMAL(19, 4) NOT NULL,
    confidence DECIMAL(5, 4) NOT NULL,
    model_version VARCHAR(50) NOT NULL,
    decision_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recovery_decisions_case ON recovery_decisions(recovery_case_id);
CREATE INDEX idx_recovery_decisions_action ON recovery_decisions(recommended_action);

CREATE TABLE counterfactual_evaluations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recovery_decision_id UUID NOT NULL REFERENCES recovery_decisions(id),
    action VARCHAR(50) NOT NULL,
    recovery_probability DECIMAL(5, 4) NOT NULL,
    no_action_probability DECIMAL(5, 4) NOT NULL,
    incremental_probability DECIMAL(5, 4) NOT NULL,
    expected_incremental_revenue DECIMAL(19, 4) NOT NULL,
    action_cost DECIMAL(19, 4) NOT NULL DEFAULT 0,
    friction_cost DECIMAL(19, 4) NOT NULL DEFAULT 0,
    expected_net_value DECIMAL(19, 4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_counterfactual_decision ON counterfactual_evaluations(recovery_decision_id);
CREATE INDEX idx_counterfactual_action ON counterfactual_evaluations(action);

CREATE TABLE recovery_actions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recovery_case_id UUID NOT NULL REFERENCES recovery_cases(id),
    action VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    blocked_reason TEXT,
    executed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recovery_actions_case ON recovery_actions(recovery_case_id);
CREATE INDEX idx_recovery_actions_status ON recovery_actions(status);

CREATE TABLE recovery_outcomes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recovery_action_id UUID NOT NULL REFERENCES recovery_actions(id),
    recovered BOOLEAN NOT NULL,
    recovered_amount DECIMAL(19, 4),
    outcome_type VARCHAR(50) NOT NULL,
    observed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_recovery_outcomes_action ON recovery_outcomes(recovery_action_id);
CREATE INDEX idx_recovery_outcomes_recovered ON recovery_outcomes(recovered);

-- Transactions table (if not exists from V1)
CREATE TABLE IF NOT EXISTS transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    amount DECIMAL(19, 4) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    payment_method VARCHAR(50) NOT NULL,
    processor VARCHAR(100),
    issuer VARCHAR(100),
    region VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Customers table (if not exists)
CREATE TABLE IF NOT EXISTS customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    merchant_id UUID NOT NULL,
    historical_success_rate DECIMAL(5, 4),
    preferred_payment_method VARCHAR(50),
    contactability_score DECIMAL(5, 4),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Merchants table (if not exists)
CREATE TABLE IF NOT EXISTS merchants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    recovery_budget_amount DECIMAL(19, 4) NOT NULL,
    max_retry_attempts INTEGER NOT NULL DEFAULT 3,
    max_contacts_per_day INTEGER NOT NULL DEFAULT 2,
    high_value_threshold DECIMAL(19, 4) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Payment attempts table (if not exists)
CREATE TABLE IF NOT EXISTS payment_attempts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    transaction_id UUID NOT NULL,
    attempt_number INTEGER NOT NULL,
    failure_code VARCHAR(100),
    latency_ms INTEGER,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
