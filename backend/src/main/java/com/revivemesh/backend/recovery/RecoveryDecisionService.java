package com.revivemesh.backend.recovery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Recovery decision orchestration service.
 * Creates recovery cases, evaluates counterfactual actions, and persists decisions.
 */
@Service
public class RecoveryDecisionService {

    private static final Logger log = LoggerFactory.getLogger(RecoveryDecisionService.class);

    private final RecoveryCaseRepository recoveryCaseRepository;
    private final RecoveryDecisionRepository recoveryDecisionRepository;
    private final CounterfactualEvaluationRepository counterfactualEvaluationRepository;
    private final CounterfactualEvaluator counterfactualEvaluator;
    private final RecoveryPredictionModel predictionModel;
    private final RecoveryEventProducer eventProducer;

    public RecoveryDecisionService(
        RecoveryCaseRepository recoveryCaseRepository,
        RecoveryDecisionRepository recoveryDecisionRepository,
        CounterfactualEvaluationRepository counterfactualEvaluationRepository,
        CounterfactualEvaluator counterfactualEvaluator,
        RecoveryPredictionModel predictionModel,
        RecoveryEventProducer eventProducer
    ) {
        this.recoveryCaseRepository = recoveryCaseRepository;
        this.recoveryDecisionRepository = recoveryDecisionRepository;
        this.counterfactualEvaluationRepository = counterfactualEvaluationRepository;
        this.counterfactualEvaluator = counterfactualEvaluator;
        this.predictionModel = predictionModel;
        this.eventProducer = eventProducer;
    }

    /**
     * Create or retrieve recovery case for a failed transaction.
     */
    @Transactional
    public RecoveryCase createRecoveryCase(UUID transactionId, boolean eligible) {
        return recoveryCaseRepository.findByTransactionId(transactionId)
            .orElseGet(() -> {
                RecoveryCase recoveryCase = new RecoveryCase(transactionId, "PENDING", eligible);
                RecoveryCase saved = recoveryCaseRepository.save(recoveryCase);
                log.info("Created recovery case {} for transaction {}", saved.getId(), transactionId);
                return saved;
            });
    }

    /**
     * Evaluate recovery options and create a decision.
     */
    @Transactional
    public RecoveryDecision evaluateAndDecide(UUID recoveryCaseId, RecoveryContext context) {
        RecoveryCase recoveryCase = recoveryCaseRepository.findById(recoveryCaseId)
            .orElseThrow(() -> new IllegalArgumentException("Recovery case not found: " + recoveryCaseId));

        if (!recoveryCase.getEligible()) {
            log.warn("Recovery case {} is not eligible for recovery", recoveryCaseId);
            throw new IllegalStateException("Recovery case is not eligible");
        }

        // Evaluate all actions
        List<ActionEvaluation> evaluations = counterfactualEvaluator.evaluateAllActions(context);
        ActionEvaluation recommended = evaluations.get(0);

        // Get model confidence
        BigDecimal confidence = predictionModel.getConfidence(context);
        String decisionReason = counterfactualEvaluator.generateDecisionReason(recommended, context);

        // Create decision record
        RecoveryDecision decision = new RecoveryDecision(
            recoveryCaseId,
            recommended.getAction().name(),
            recommended.getExpectedNetValue(),
            confidence,
            predictionModel.getModelVersion(),
            decisionReason
        );
        RecoveryDecision savedDecision = recoveryDecisionRepository.save(decision);

        // Persist all counterfactual evaluations
        BigDecimal doNothingProb = evaluations.stream()
            .filter(e -> e.getAction() == RecoveryAction.DO_NOTHING)
            .findFirst()
            .map(ActionEvaluation::getRecoveryProbability)
            .orElse(BigDecimal.ZERO);

        for (ActionEvaluation eval : evaluations) {
            CounterfactualEvaluation cfEval = new CounterfactualEvaluation(
                savedDecision.getId(),
                eval.getAction().name(),
                eval.getRecoveryProbability(),
                doNothingProb,
                eval.getIncrementalProbability(),
                eval.getExpectedIncrementalRevenue(),
                eval.getActionCost(),
                eval.getFrictionCost(),
                eval.getExpectedNetValue()
            );
            counterfactualEvaluationRepository.save(cfEval);
        }

        // Update case state
        recoveryCase.setState("EVALUATED");
        recoveryCaseRepository.save(recoveryCase);

        // Emit event
        eventProducer.publishRecoveryDecisionCreated(savedDecision, context);

        log.info("Created recovery decision {} for case {} - recommended action: {}",
            savedDecision.getId(), recoveryCaseId, recommended.getAction());

        return savedDecision;
    }

    /**
     * Get recovery decision with all counterfactual evaluations.
     */
    @Transactional(readOnly = true)
    public RecoveryDecisionDetail getDecisionDetail(UUID decisionId) {
        RecoveryDecision decision = recoveryDecisionRepository.findById(decisionId)
            .orElseThrow(() -> new IllegalArgumentException("Decision not found: " + decisionId));

        List<CounterfactualEvaluation> evaluations =
            counterfactualEvaluationRepository.findByRecoveryDecisionId(decisionId);

        return new RecoveryDecisionDetail(decision, evaluations);
    }

    /**
     * Get all decisions for a recovery case.
     */
    @Transactional(readOnly = true)
    public List<RecoveryDecision> getDecisionsByCase(UUID recoveryCaseId) {
        return recoveryDecisionRepository.findByRecoveryCaseId(recoveryCaseId);
    }
}
