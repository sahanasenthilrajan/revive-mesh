package com.revivemesh.backend.recovery;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * REST API for recovery decisions and counterfactual evaluations.
 */
@RestController
@RequestMapping("/api/recovery")
public class RecoveryController {

    private final RecoveryDecisionService recoveryDecisionService;
    private final RecoveryCaseRepository recoveryCaseRepository;

    public RecoveryController(RecoveryDecisionService recoveryDecisionService,
                              RecoveryCaseRepository recoveryCaseRepository) {
        this.recoveryDecisionService = recoveryDecisionService;
        this.recoveryCaseRepository = recoveryCaseRepository;
    }

    /**
     * Create a recovery case for a failed transaction.
     */
    @PostMapping("/cases")
    public ResponseEntity<RecoveryCase> createRecoveryCase(@RequestBody CreateRecoveryCaseRequest request) {
        RecoveryCase recoveryCase = recoveryDecisionService.createRecoveryCase(
            request.transactionId(),
            request.eligible()
        );
        return ResponseEntity.ok(recoveryCase);
    }

    /**
     * Evaluate recovery options and create decision.
     */
    @PostMapping("/cases/{caseId}/evaluate")
    public ResponseEntity<RecoveryDecision> evaluateRecoveryCase(
        @PathVariable UUID caseId,
        @RequestBody RecoveryContextRequest contextRequest
    ) {
        RecoveryContext context = buildContext(contextRequest);
        RecoveryDecision decision = recoveryDecisionService.evaluateAndDecide(caseId, context);
        return ResponseEntity.ok(decision);
    }

    /**
     * Get decision detail with all counterfactual evaluations.
     */
    @GetMapping("/decisions/{decisionId}")
    public ResponseEntity<RecoveryDecisionDetail> getDecisionDetail(@PathVariable UUID decisionId) {
        RecoveryDecisionDetail detail = recoveryDecisionService.getDecisionDetail(decisionId);
        return ResponseEntity.ok(detail);
    }

    /**
     * Get all decisions for a recovery case.
     */
    @GetMapping("/cases/{caseId}/decisions")
    public ResponseEntity<List<RecoveryDecision>> getDecisionsByCase(@PathVariable UUID caseId) {
        List<RecoveryDecision> decisions = recoveryDecisionService.getDecisionsByCase(caseId);
        return ResponseEntity.ok(decisions);
    }

    /**
     * Get recovery case by transaction ID.
     */
    @GetMapping("/cases/transaction/{transactionId}")
    public ResponseEntity<RecoveryCase> getRecoveryCaseByTransaction(@PathVariable UUID transactionId) {
        return recoveryCaseRepository.findByTransactionId(transactionId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    private RecoveryContext buildContext(RecoveryContextRequest req) {
        RecoveryContext context = new RecoveryContext();
        context.setTransactionId(req.transactionId());
        context.setCustomerId(req.customerId());
        context.setMerchantId(req.merchantId());
        context.setAmount(req.amount());
        context.setPaymentMethod(req.paymentMethod());
        context.setProcessor(req.processor());
        context.setIssuer(req.issuer());
        context.setRegion(req.region());
        context.setFailureCode(req.failureCode());
        context.setAttemptNumber(req.attemptNumber());
        context.setCustomerHistoricalSuccess(req.customerHistoricalSuccess());
        context.setRecentContactCount(req.recentContactCount());
        context.setIncidentActive(req.incidentActive());
        context.setIncidentFingerprint(req.incidentFingerprint());
        return context;
    }
}

record CreateRecoveryCaseRequest(UUID transactionId, boolean eligible) {}

record RecoveryContextRequest(
    UUID transactionId,
    UUID customerId,
    UUID merchantId,
    BigDecimal amount,
    String paymentMethod,
    String processor,
    String issuer,
    String region,
    String failureCode,
    Integer attemptNumber,
    BigDecimal customerHistoricalSuccess,
    Integer recentContactCount,
    Boolean incidentActive,
    String incidentFingerprint
) {}
