package com.revivemesh.backend.recovery;

import java.util.List;

public class RecoveryDecisionDetail {
    private RecoveryDecision decision;
    private List<CounterfactualEvaluation> evaluations;

    public RecoveryDecisionDetail(RecoveryDecision decision, List<CounterfactualEvaluation> evaluations) {
        this.decision = decision;
        this.evaluations = evaluations;
    }

    public RecoveryDecision getDecision() {
        return decision;
    }

    public List<CounterfactualEvaluation> getEvaluations() {
        return evaluations;
    }
}
