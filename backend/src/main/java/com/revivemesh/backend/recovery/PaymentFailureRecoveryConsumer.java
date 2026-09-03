package com.revivemesh.backend.recovery;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

/**
 * Consumes payment failure events and creates recovery cases.
 */
@Component
public class PaymentFailureRecoveryConsumer {

    private static final Logger log = LoggerFactory.getLogger(PaymentFailureRecoveryConsumer.class);

    private final RecoveryDecisionService recoveryDecisionService;
    private final ObjectMapper objectMapper;

    public PaymentFailureRecoveryConsumer(RecoveryDecisionService recoveryDecisionService, ObjectMapper objectMapper) {
        this.recoveryDecisionService = recoveryDecisionService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "payment-failures", groupId = "recovery-engine")
    public void consumePaymentFailure(String message) {
        try {
            EventEnvelope<?> envelope = objectMapper.readValue(message, EventEnvelope.class);
            Map<String, Object> payload = (Map<String, Object>) envelope.getPayload();

            UUID transactionId = UUID.fromString((String) payload.get("transactionId"));
            UUID customerId = UUID.fromString((String) payload.get("customerId"));
            UUID merchantId = envelope.getMerchantId() != null ? UUID.fromString(envelope.getMerchantId()) : null;

            BigDecimal amount = new BigDecimal(payload.get("amount").toString());
            String paymentMethod = (String) payload.get("paymentMethod");
            String processor = (String) payload.get("processor");
            String issuer = (String) payload.get("issuer");
            String region = (String) payload.get("region");
            String failureCode = (String) payload.get("failureCode");
            Integer attemptNumber = (Integer) payload.get("attemptNumber");

            // Create recovery case
            boolean eligible = isEligibleForRecovery(failureCode, attemptNumber);
            RecoveryCase recoveryCase = recoveryDecisionService.createRecoveryCase(transactionId, eligible);

            if (eligible) {
                // Build context and evaluate
                RecoveryContext context = new RecoveryContext();
                context.setTransactionId(transactionId);
                context.setCustomerId(customerId);
                context.setMerchantId(merchantId);
                context.setAmount(amount);
                context.setPaymentMethod(paymentMethod);
                context.setProcessor(processor);
                context.setIssuer(issuer);
                context.setRegion(region);
                context.setFailureCode(failureCode);
                context.setAttemptNumber(attemptNumber);
                context.setCustomerHistoricalSuccess(new BigDecimal("0.75")); // TODO: fetch from customer service
                context.setRecentContactCount(0); // TODO: fetch from contact history
                context.setIncidentActive(false); // TODO: check incident service

                recoveryDecisionService.evaluateAndDecide(recoveryCase.getId(), context);
            } else {
                log.info("Transaction {} not eligible for recovery: failureCode={}, attemptNumber={}",
                    transactionId, failureCode, attemptNumber);
            }

        } catch (Exception e) {
            log.error("Failed to process payment failure event", e);
        }
    }

    private boolean isEligibleForRecovery(String failureCode, Integer attemptNumber) {
        // Do not recover after too many attempts
        if (attemptNumber != null && attemptNumber > 5) {
            return false;
        }

        // Some failure codes are not recoverable
        if ("FRAUD_SUSPECTED".equals(failureCode) || "ACCOUNT_CLOSED".equals(failureCode)) {
            return false;
        }

        return true;
    }
}
