package com.revivemesh.backend.simulator;

import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.EventProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

/**
 * Deterministic payment failure simulator for demo scenarios.
 */
@Service
public class PaymentSimulator {

    private static final Logger log = LoggerFactory.getLogger(PaymentSimulator.class);
    private final EventProducer eventProducer;
    private final Random random;

    public PaymentSimulator(EventProducer eventProducer) {
        this.eventProducer = eventProducer;
        this.random = new Random(42); // Fixed seed for reproducibility
    }

    /**
     * SCENARIO 1: Normal payment failures - low baseline failure rate.
     */
    public void runScenario1_NormalFailures() {
        log.info("Running SCENARIO 1: Normal payment failures");
        UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // Generate 100 payments with ~5% failure rate
        for (int i = 0; i < 100; i++) {
            boolean failed = random.nextDouble() < 0.05;
            if (failed) {
                emitPaymentFailure(merchantId, randomFailureCode(), randomProcessor(), randomIssuer(), 1);
            }
        }
    }

    /**
     * SCENARIO 2: Systemic gateway failure causing Failure Swarm.
     */
    public void runScenario2_FailureSwarm() {
        log.info("Running SCENARIO 2: Failure Swarm - systemic gateway outage");
        UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // 50 failures concentrated on GATEWAY_A + ISSUER_TIMEOUT
        for (int i = 0; i < 50; i++) {
            emitPaymentFailure(merchantId, "ISSUER_TIMEOUT", "GATEWAY_A", "BANK_X", 1);
        }
    }

    /**
     * SCENARIO 3: Many failed transactions competing for limited Recovery Budget.
     */
    public void runScenario3_BudgetCompetition() {
        log.info("Running SCENARIO 3: Recovery Budget competition");
        UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // 200 eligible failures with varying transaction amounts
        for (int i = 0; i < 200; i++) {
            BigDecimal amount = BigDecimal.valueOf(50 + random.nextInt(1000));
            emitPaymentFailureWithAmount(merchantId, "INSUFFICIENT_FUNDS", "GATEWAY_B",
                "BANK_Y", 1, amount);
        }
    }

    /**
     * SCENARIO 4: DO_NOTHING beats intervention.
     */
    public void runScenario4_DoNothingWins() {
        log.info("Running SCENARIO 4: DO_NOTHING counterfactual winner");
        UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // High-friction failure: CARD_EXPIRED
        emitPaymentFailure(merchantId, "CARD_EXPIRED", "GATEWAY_C", "BANK_Z", 3);
    }

    /**
     * SCENARIO 5: Intervention beats DO_NOTHING.
     */
    public void runScenario5_InterventionWins() {
        log.info("Running SCENARIO 5: Intervention counterfactual winner");
        UUID merchantId = UUID.fromString("00000000-0000-0000-0000-000000000001");

        // Recoverable failure: INSUFFICIENT_FUNDS, first attempt, high value
        emitPaymentFailureWithAmount(merchantId, "INSUFFICIENT_FUNDS", "GATEWAY_D",
            "BANK_W", 1, new BigDecimal("500.00"));
    }

    private void emitPaymentFailure(UUID merchantId, String failureCode, String processor,
                                   String issuer, int attemptNumber) {
        emitPaymentFailureWithAmount(merchantId, failureCode, processor, issuer, attemptNumber,
            BigDecimal.valueOf(100 + random.nextInt(400)));
    }

    private void emitPaymentFailureWithAmount(UUID merchantId, String failureCode, String processor,
                                             String issuer, int attemptNumber, BigDecimal amount) {
        UUID transactionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Map<String, Object> payload = new HashMap<>();
        payload.put("transactionId", transactionId.toString());
        payload.put("customerId", customerId.toString());
        payload.put("amount", amount.toString());
        payload.put("paymentMethod", "CARD");
        payload.put("processor", processor);
        payload.put("issuer", issuer);
        payload.put("region", "US");
        payload.put("failureCode", failureCode);
        payload.put("attemptNumber", attemptNumber);
        payload.put("timestamp", Instant.now().toString());

        EventEnvelope<Map<String, Object>> envelope = new EventEnvelope<>(
            UUID.randomUUID(),
            "PaymentFailedEvent",
            Instant.now(),
            "1.0",
            transactionId,
            merchantId,
            payload
        );

        eventProducer.sendEvent("payment-failures", transactionId.toString(), envelope);
    }

    private String randomFailureCode() {
        String[] codes = {"INSUFFICIENT_FUNDS", "ISSUER_TIMEOUT", "CARD_DECLINED", "NETWORK_ERROR"};
        return codes[random.nextInt(codes.length)];
    }

    private String randomProcessor() {
        String[] processors = {"GATEWAY_A", "GATEWAY_B", "GATEWAY_C", "GATEWAY_D"};
        return processors[random.nextInt(processors.length)];
    }

    private String randomIssuer() {
        String[] issuers = {"BANK_X", "BANK_Y", "BANK_Z", "BANK_W"};
        return issuers[random.nextInt(issuers.length)];
    }
}
