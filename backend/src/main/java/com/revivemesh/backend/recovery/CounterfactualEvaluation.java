package com.revivemesh.backend.recovery;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "counterfactual_evaluations")
public class CounterfactualEvaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recovery_decision_id", nullable = false)
    private UUID recoveryDecisionId;

    @Column(nullable = false, length = 50)
    private String action;

    @Column(name = "recovery_probability", nullable = false, precision = 5, scale = 4)
    private BigDecimal recoveryProbability;

    @Column(name = "no_action_probability", nullable = false, precision = 5, scale = 4)
    private BigDecimal noActionProbability;

    @Column(name = "incremental_probability", nullable = false, precision = 5, scale = 4)
    private BigDecimal incrementalProbability;

    @Column(name = "expected_incremental_revenue", nullable = false, precision = 19, scale = 4)
    private BigDecimal expectedIncrementalRevenue;

    @Column(name = "action_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal actionCost = BigDecimal.ZERO;

    @Column(name = "friction_cost", nullable = false, precision = 19, scale = 4)
    private BigDecimal frictionCost = BigDecimal.ZERO;

    @Column(name = "expected_net_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal expectedNetValue;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public CounterfactualEvaluation() {}

    public CounterfactualEvaluation(UUID recoveryDecisionId, String action, BigDecimal recoveryProbability,
                                    BigDecimal noActionProbability, BigDecimal incrementalProbability,
                                    BigDecimal expectedIncrementalRevenue, BigDecimal actionCost,
                                    BigDecimal frictionCost, BigDecimal expectedNetValue) {
        this.recoveryDecisionId = recoveryDecisionId;
        this.action = action;
        this.recoveryProbability = recoveryProbability;
        this.noActionProbability = noActionProbability;
        this.incrementalProbability = incrementalProbability;
        this.expectedIncrementalRevenue = expectedIncrementalRevenue;
        this.actionCost = actionCost;
        this.frictionCost = frictionCost;
        this.expectedNetValue = expectedNetValue;
    }

    public UUID getId() {
        return id;
    }

    public UUID getRecoveryDecisionId() {
        return recoveryDecisionId;
    }

    public String getAction() {
        return action;
    }

    public BigDecimal getRecoveryProbability() {
        return recoveryProbability;
    }

    public BigDecimal getNoActionProbability() {
        return noActionProbability;
    }

    public BigDecimal getIncrementalProbability() {
        return incrementalProbability;
    }

    public BigDecimal getExpectedIncrementalRevenue() {
        return expectedIncrementalRevenue;
    }

    public BigDecimal getActionCost() {
        return actionCost;
    }

    public BigDecimal getFrictionCost() {
        return frictionCost;
    }

    public BigDecimal getExpectedNetValue() {
        return expectedNetValue;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
