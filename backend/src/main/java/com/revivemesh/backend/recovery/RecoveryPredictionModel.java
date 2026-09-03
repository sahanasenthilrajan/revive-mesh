package com.revivemesh.backend.recovery;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Recovery prediction model interface.
 * Returns probability estimates for each action given transaction context.
 */
public interface RecoveryPredictionModel {

    /**
     * Predict recovery probability for each action.
     * @param context Transaction and customer context
     * @return Map of action to recovery probability [0.0, 1.0]
     */
    Map<RecoveryAction, BigDecimal> predictRecoveryProbabilities(RecoveryContext context);

    /**
     * Get model version identifier
     */
    String getModelVersion();

    /**
     * Get model confidence score
     */
    BigDecimal getConfidence(RecoveryContext context);
}
