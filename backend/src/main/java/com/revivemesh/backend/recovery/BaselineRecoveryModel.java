package com.revivemesh.backend.recovery;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

/**
 * MVP recovery prediction model using calibrated heuristics.
 * This is a synthetic baseline model for demonstration purposes.
 *
 * In production, this would be replaced with a trained ML model
 * using historical recovery outcome data.
 */
@Component
public class BaselineRecoveryModel implements RecoveryPredictionModel {

    private static final String MODEL_VERSION = "baseline-v1.0";
    private static final BigDecimal BASE_DO_NOTHING_PROBABILITY = new BigDecimal("0.08");

    @Override
    public Map<RecoveryAction, BigDecimal> predictRecoveryProbabilities(RecoveryContext context) {
        Map<RecoveryAction, BigDecimal> predictions = new HashMap<>();

        // Base DO_NOTHING probability (organic recovery)
        BigDecimal doNothingProb = calculateDoNothingProbability(context);
        predictions.put(RecoveryAction.DO_NOTHING, doNothingProb);

        // Calculate action-specific probabilities
        predictions.put(RecoveryAction.RETRY_30M, calculateRetry30MProb(context, doNothingProb));
        predictions.put(RecoveryAction.RETRY_TOMORROW, calculateRetryTomorrowProb(context, doNothingProb));
        predictions.put(RecoveryAction.PAYMENT_LINK, calculatePaymentLinkProb(context, doNothingProb));
        predictions.put(RecoveryAction.ALTERNATE_METHOD, calculateAlternateMethodProb(context, doNothingProb));
        predictions.put(RecoveryAction.CUSTOMER_CONTACT, calculateCustomerContactProb(context, doNothingProb));

        return predictions;
    }

    @Override
    public String getModelVersion() {
        return MODEL_VERSION;
    }

    @Override
    public BigDecimal getConfidence(RecoveryContext context) {
        // Simple confidence based on data completeness
        int featureCount = 0;
        if (context.getCustomerHistoricalSuccess() != null) featureCount++;
        if (context.getFailureCode() != null) featureCount++;
        if (context.getProcessor() != null) featureCount++;
        if (context.getAttemptNumber() != null) featureCount++;

        return new BigDecimal(0.6 + (featureCount * 0.08)).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateDoNothingProbability(RecoveryContext context) {
        BigDecimal prob = BASE_DO_NOTHING_PROBABILITY;

        // Higher historical success -> higher organic recovery
        if (context.getCustomerHistoricalSuccess() != null) {
            prob = prob.add(context.getCustomerHistoricalSuccess().multiply(new BigDecimal("0.05")));
        }

        // Transient failures more likely to self-resolve
        if ("INSUFFICIENT_FUNDS".equals(context.getFailureCode())) {
            prob = prob.add(new BigDecimal("0.12"));
        } else if ("CARD_EXPIRED".equals(context.getFailureCode())) {
            prob = prob.add(new BigDecimal("0.02"));
        }

        return prob.min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRetry30MProb(RecoveryContext context, BigDecimal baseProb) {
        BigDecimal lift = new BigDecimal("0.18");

        // Lower lift if already multiple attempts
        if (context.getAttemptNumber() != null && context.getAttemptNumber() > 2) {
            lift = lift.multiply(new BigDecimal("0.6"));
        }

        // Lower lift if incident active (systemic issue)
        if (Boolean.TRUE.equals(context.getIncidentActive())) {
            lift = lift.multiply(new BigDecimal("0.3"));
        }

        return baseProb.add(lift).min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateRetryTomorrowProb(RecoveryContext context, BigDecimal baseProb) {
        BigDecimal lift = new BigDecimal("0.25");

        // Higher lift for insufficient funds (customer may add funds)
        if ("INSUFFICIENT_FUNDS".equals(context.getFailureCode())) {
            lift = lift.add(new BigDecimal("0.15"));
        }

        // Lower lift if incident active
        if (Boolean.TRUE.equals(context.getIncidentActive())) {
            lift = lift.multiply(new BigDecimal("0.4"));
        }

        return baseProb.add(lift).min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculatePaymentLinkProb(RecoveryContext context, BigDecimal baseProb) {
        BigDecimal lift = new BigDecimal("0.22");

        // Higher lift for card-specific issues
        if ("CARD".equals(context.getPaymentMethod())) {
            lift = lift.add(new BigDecimal("0.08"));
        }

        // Customer contactability matters
        if (context.getRecentContactCount() != null && context.getRecentContactCount() > 1) {
            lift = lift.multiply(new BigDecimal("0.7")); // Fatigue
        }

        return baseProb.add(lift).min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateAlternateMethodProb(RecoveryContext context, BigDecimal baseProb) {
        BigDecimal lift = new BigDecimal("0.32");

        // Strong lift for payment-method-specific failures
        if ("CARD_DECLINED".equals(context.getFailureCode()) ||
            "CARD_EXPIRED".equals(context.getFailureCode())) {
            lift = lift.add(new BigDecimal("0.12"));
        }

        // Customer must have alternate method available (assume moderate probability)
        lift = lift.multiply(new BigDecimal("0.75"));

        return baseProb.add(lift).min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCustomerContactProb(RecoveryContext context, BigDecimal baseProb) {
        BigDecimal lift = new BigDecimal("0.28");

        // High-value transactions justify contact effort
        if (context.getAmount() != null && context.getAmount().compareTo(new BigDecimal("100")) > 0) {
            lift = lift.add(new BigDecimal("0.10"));
        }

        // Contact fatigue
        if (context.getRecentContactCount() != null && context.getRecentContactCount() > 0) {
            lift = lift.multiply(new BigDecimal("0.6"));
        }

        return baseProb.add(lift).min(new BigDecimal("0.95")).setScale(4, RoundingMode.HALF_UP);
    }
}
