package com.revivemesh.backend.recovery;

import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.EventProducer;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Produces recovery-related Kafka events.
 */
@Component
public class RecoveryEventProducer {

    private final EventProducer eventProducer;

    public RecoveryEventProducer(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
    }

    public void publishRecoveryDecisionCreated(RecoveryDecision decision, RecoveryContext context) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("decisionId", decision.getId().toString());
        payload.put("transactionId", context.getTransactionId().toString());
        payload.put("recommendedAction", decision.getRecommendedAction());
        payload.put("expectedNetValue", decision.getExpectedNetValue());
        payload.put("confidence", decision.getConfidence());
        payload.put("modelVersion", decision.getModelVersion());

        EventEnvelope<Map<String, Object>> envelope = new EventEnvelope<>(
            UUID.randomUUID(),
            "RecoveryDecisionCreatedEvent",
            Instant.now(),
            "1.0",
            context.getTransactionId(),
            context.getMerchantId(),
            payload
        );

        eventProducer.publish("recovery-decisions", envelope);
    }

    public void publishRecoveryActionRequested(UUID actionId, UUID decisionId, UUID transactionId,
                                               String action, String idempotencyKey) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("actionId", actionId.toString());
        payload.put("decisionId", decisionId.toString());
        payload.put("transactionId", transactionId.toString());
        payload.put("action", action);
        payload.put("idempotencyKey", idempotencyKey);

        EventEnvelope<Map<String, Object>> envelope = new EventEnvelope<>(
            UUID.randomUUID(),
            "RecoveryActionRequestedEvent",
            Instant.now(),
            "1.0",
            transactionId,
            null, // merchantId can be added if available
            payload
        );

        eventProducer.publish("recovery-actions", envelope);
    }
}
