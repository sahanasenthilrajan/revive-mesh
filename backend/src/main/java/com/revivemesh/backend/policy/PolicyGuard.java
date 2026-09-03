package com.revivemesh.backend.policy;

import com.revivemesh.backend.recovery.RecoveryAction;
import com.revivemesh.backend.recovery.RecoveryContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

/**
 * Policy enforcement layer that blocks unsafe recovery actions.
 */
@Service
public class PolicyGuard {

    private static final Logger log = LoggerFactory.getLogger(PolicyGuard.class);

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final int MAX_CUSTOMER_CONTACTS_PER_DAY = 2;
    private static final String HIGH_VALUE_THRESHOLD = "10000.00";

    private final RedisTemplate<String, String> redisTemplate;

    public PolicyGuard(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Evaluate whether a recovery action should be allowed.
     */
    public PolicyDecision evaluate(RecoveryAction action, RecoveryContext context) {

        // Policy 1: Max retry attempts
        if (isRetryAction(action) && context.getAttemptNumber() >= MAX_RETRY_ATTEMPTS) {
            log.info("Policy BLOCK: MAX_RETRY_ATTEMPTS for transaction {}", context.getTransactionId());
            return PolicyDecision.block("MAX_RETRY_ATTEMPTS",
                String.format("Attempt %d exceeds limit of %d", context.getAttemptNumber(), MAX_RETRY_ATTEMPTS));
        }

        // Policy 2: Non-recoverable failure codes
        if (isNonRecoverableFailure(context.getFailureCode())) {
            log.info("Policy BLOCK: NON_RECOVERABLE_FAILURE {} for transaction {}",
                context.getFailureCode(), context.getTransactionId());
            return PolicyDecision.block("NON_RECOVERABLE_FAILURE",
                String.format("Failure code %s cannot be recovered", context.getFailureCode()));
        }

        // Policy 3: Customer contact frequency
        if (isContactAction(action)) {
            String contactKey = contactFrequencyKey(context.getCustomerId());
            Long contactCount = redisTemplate.opsForValue().increment(contactKey, 0);

            if (contactCount != null && contactCount >= MAX_CUSTOMER_CONTACTS_PER_DAY) {
                log.info("Policy BLOCK: CONTACT_FREQUENCY for customer {}", context.getCustomerId());
                return PolicyDecision.block("CONTACT_FREQUENCY",
                    String.format("Customer already contacted %d times today (max: %d)",
                        contactCount, MAX_CUSTOMER_CONTACTS_PER_DAY));
            }

            // Increment and set expiry for midnight UTC
            redisTemplate.opsForValue().increment(contactKey, 1);
            long secondsUntilMidnight = secondsUntilMidnightUTC();
            redisTemplate.expire(contactKey, secondsUntilMidnight, TimeUnit.SECONDS);
        }

        // Policy 4: Active incident suppression for retry actions
        if (isRetryAction(action) && Boolean.TRUE.equals(context.getIncidentActive())) {
            log.info("Policy BLOCK: ACTIVE_INCIDENT_SUPPRESSION for transaction {}", context.getTransactionId());
            return PolicyDecision.block("ACTIVE_INCIDENT_SUPPRESSION",
                "Retry suppressed during active systemic incident");
        }

        // Policy 5: High-value transaction safeguard
        if (context.getAmount().compareTo(new java.math.BigDecimal(HIGH_VALUE_THRESHOLD)) > 0) {
            log.info("Policy BLOCK: HIGH_VALUE_APPROVAL_REQUIRED for transaction {}", context.getTransactionId());
            return PolicyDecision.block("HIGH_VALUE_APPROVAL_REQUIRED",
                String.format("Transaction amount %s exceeds threshold %s and requires manual review",
                    context.getAmount(), HIGH_VALUE_THRESHOLD));
        }

        return PolicyDecision.allow();
    }

    private boolean isRetryAction(RecoveryAction action) {
        return action == RecoveryAction.RETRY_30M || action == RecoveryAction.RETRY_TOMORROW;
    }

    private boolean isContactAction(RecoveryAction action) {
        return action == RecoveryAction.CUSTOMER_CONTACT || action == RecoveryAction.PAYMENT_LINK;
    }

    private boolean isNonRecoverableFailure(String failureCode) {
        return "FRAUD_SUSPECTED".equals(failureCode)
            || "ACCOUNT_CLOSED".equals(failureCode)
            || "CARD_EXPIRED".equals(failureCode)
            || "STOLEN_CARD".equals(failureCode);
    }

    private String contactFrequencyKey(java.util.UUID customerId) {
        String today = LocalDate.now(ZoneOffset.UTC).toString();
        return String.format("contact:freq:%s:%s", customerId, today);
    }

    private long secondsUntilMidnightUTC() {
        java.time.Instant now = java.time.Instant.now();
        java.time.Instant midnight = LocalDate.now(ZoneOffset.UTC)
            .plusDays(1)
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant();
        return java.time.Duration.between(now, midnight).getSeconds();
    }
}
