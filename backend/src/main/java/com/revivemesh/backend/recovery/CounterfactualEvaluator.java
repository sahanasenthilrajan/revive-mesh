package com.revivemesh.backend.recovery;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Core counterfactual evaluation engine.
 *
 * Calculates expected net value for each recovery action by:
 * 1. Getting recovery probability for each action from prediction model
 * 2. Computing incremental probability vs DO_NOTHING baseline
 * 3. Calculating expected incremental revenue
 * 4. Subtracting action costs and friction costs
 * 5. Returning ranked evaluations
 */
@Service
public class CounterfactualEvaluator {

    private final RecoveryPredictionModel predictionModel;

    // Action costs (in currency units)
    private static final Map<RecoveryAction, BigDecimal> ACTION_COSTS = Map.of(
        RecoveryAction.DO_NOTHING, BigDecimal.ZERO,
        RecoveryAction.RETRY_30M, new BigDecimal("0.05"),
        RecoveryAction.RETRY_TOMORROW, new BigDecimal("0.05"),
        RecoveryAction.PAYMENT_LINK, new BigDecimal("0.15"),
        RecoveryAction.ALTERNATE_METHOD, new BigDecimal("0.20"),
        RecoveryAction.CUSTOMER_CONTACT, new BigDecimal("2.50")
    );

    // Friction costs (customer experience cost)
    private static final Map<RecoveryAction, BigDecimal> FRICTION_COSTS = Map.of(
        RecoveryAction.DO_NOTHING, BigDecimal.ZERO,
        RecoveryAction.RETRY_30M, new BigDecimal("0.10"),
        RecoveryAction.RETRY_TOMORROW, new BigDecimal("0.05"),
        RecoveryAction.PAYMENT_LINK, new BigDecimal("0.50"),
        RecoveryAction.ALTERNATE_METHOD, new BigDecimal("1.00"),
        RecoveryAction.CUSTOMER_CONTACT, new BigDecimal("3.00")
    );

    public CounterfactualEvaluator(RecoveryPredictionModel predictionModel) {
        this.predictionModel = predictionModel;
    }

    /**
     * Evaluate all recovery actions for the given context.
     * Returns list sorted by expected net value (descending).
     */
    public List<ActionEvaluation> evaluateAllActions(RecoveryContext context) {
        Map<RecoveryAction, BigDecimal> probabilities = predictionModel.predictRecoveryProbabilities(context);
        BigDecimal doNothingProb = probabilities.get(RecoveryAction.DO_NOTHING);
        BigDecimal transactionAmount = context.getAmount();

        List<ActionEvaluation> evaluations = new ArrayList<>();

        for (RecoveryAction action : RecoveryAction.values()) {
            BigDecimal recoveryProb = probabilities.get(action);
            BigDecimal incrementalProb = recoveryProb.subtract(doNothingProb)
                .max(BigDecimal.ZERO)
                .setScale(4, RoundingMode.HALF_UP);

            BigDecimal expectedIncrementalRevenue = incrementalProb.multiply(transactionAmount)
                .setScale(4, RoundingMode.HALF_UP);

            BigDecimal actionCost = ACTION_COSTS.getOrDefault(action, BigDecimal.ZERO);
            BigDecimal frictionCost = FRICTION_COSTS.getOrDefault(action, BigDecimal.ZERO);

            BigDecimal expectedNetValue = expectedIncrementalRevenue
                .subtract(actionCost)
                .subtract(frictionCost)
                .setScale(4, RoundingMode.HALF_UP);

            evaluations.add(new ActionEvaluation(
                action,
                recoveryProb,
                incrementalProb,
                expectedIncrementalRevenue,
                actionCost,
                frictionCost,
                expectedNetValue
            ));
        }

        // Sort by expected net value descending
        evaluations.sort(Comparator.comparing(ActionEvaluation::getExpectedNetValue).reversed());

        return evaluations;
    }

    /**
     * Get the recommended action (highest expected net value).
     */
    public ActionEvaluation getRecommendedAction(RecoveryContext context) {
        List<ActionEvaluation> evaluations = evaluateAllActions(context);
        return evaluations.isEmpty() ? null : evaluations.get(0);
    }

    /**
     * Calculate decision reason explaining the recommendation.
     */
    public String generateDecisionReason(ActionEvaluation recommended, RecoveryContext context) {
        if (recommended.getAction() == RecoveryAction.DO_NOTHING) {
            return String.format(
                "DO_NOTHING selected: incremental value of interventions is negative or too low. " +
                "Organic recovery probability: %.2f%%",
                recommended.getRecoveryProbability().multiply(new BigDecimal("100"))
            );
        }

        return String.format(
            "%s selected: highest expected net value $%.2f (incremental recovery %.2f%%, revenue $%.2f, costs $%.2f)",
            recommended.getAction().name(),
            recommended.getExpectedNetValue(),
            recommended.getIncrementalProbability().multiply(new BigDecimal("100")),
            recommended.getExpectedIncrementalRevenue(),
            recommended.getActionCost().add(recommended.getFrictionCost())
        );
    }
}
