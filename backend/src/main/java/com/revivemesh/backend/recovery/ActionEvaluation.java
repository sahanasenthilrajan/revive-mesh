package com.revivemesh.backend.recovery;

import java.math.BigDecimal;

public class ActionEvaluation {
    private RecoveryAction action;
    private BigDecimal recoveryProbability;
    private BigDecimal incrementalProbability;
    private BigDecimal expectedIncrementalRevenue;
    private BigDecimal actionCost;
    private BigDecimal frictionCost;
    private BigDecimal expectedNetValue;

    public ActionEvaluation(RecoveryAction action, BigDecimal recoveryProbability, BigDecimal incrementalProbability,
                            BigDecimal expectedIncrementalRevenue, BigDecimal actionCost, BigDecimal frictionCost,
                            BigDecimal expectedNetValue) {
        this.action = action;
        this.recoveryProbability = recoveryProbability;
        this.incrementalProbability = incrementalProbability;
        this.expectedIncrementalRevenue = expectedIncrementalRevenue;
        this.actionCost = actionCost;
        this.frictionCost = frictionCost;
        this.expectedNetValue = expectedNetValue;
    }

    public RecoveryAction getAction() {
        return action;
    }

    public BigDecimal getRecoveryProbability() {
        return recoveryProbability;
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
}
