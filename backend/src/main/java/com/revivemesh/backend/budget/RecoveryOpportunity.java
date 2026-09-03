package com.revivemesh.backend.budget;

import java.math.BigDecimal;
import java.util.UUID;

public class RecoveryOpportunity {
    private UUID caseId;
    private BigDecimal expectedNetValue;
    private BigDecimal actionCost;
    private String action;

    public RecoveryOpportunity(UUID caseId, BigDecimal expectedNetValue, BigDecimal actionCost, String action) {
        this.caseId = caseId;
        this.expectedNetValue = expectedNetValue;
        this.actionCost = actionCost;
        this.action = action;
    }

    public UUID getCaseId() {
        return caseId;
    }

    public BigDecimal getExpectedNetValue() {
        return expectedNetValue;
    }

    public BigDecimal getActionCost() {
        return actionCost;
    }

    public String getAction() {
        return action;
    }
}
