CREATE TABLE failure_swarm_incidents (
    swarm_id VARCHAR(255) PRIMARY KEY,
    fingerprint VARCHAR(255) NOT NULL,
    affected_transaction_count INT NOT NULL,
    failure_rate DOUBLE PRECISION NOT NULL,
    baseline_failure_rate DOUBLE PRECISION NOT NULL,
    confidence DOUBLE PRECISION NOT NULL,
    affected_dimensions TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    detected_at TIMESTAMP WITH TIME ZONE NOT NULL
);
