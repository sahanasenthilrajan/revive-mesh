package com.revivemesh.backend.budget;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Recovery budget allocation service using Redis for atomic operations.
 */
@Service
public class RecoveryBudgetService {

    private static final Logger log = LoggerFactory.getLogger(RecoveryBudgetService.class);
    private static final BigDecimal DEFAULT_DAILY_BUDGET = new BigDecimal("500.0");

    private final RedisTemplate<String, String> redisTemplate;

    public RecoveryBudgetService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Attempt to allocate budget for a recovery case.
     * Returns true if budget was successfully reserved, false if insufficient.
     */
    public boolean allocateBudget(UUID merchantId, UUID caseId, BigDecimal cost) {
        String budgetKey = budgetKey(merchantId);
        String allocationKey = allocationKey(merchantId, caseId);

        // Check for duplicate allocation (idempotency)
        Boolean exists = redisTemplate.hasKey(allocationKey);
        if (Boolean.TRUE.equals(exists)) {
            log.info("Budget already allocated for case {}", caseId);
            return true;
        }

        // Atomic decrement
        Long remaining = redisTemplate.opsForValue().decrement(budgetKey, cost.longValue());

        if (remaining == null || remaining < 0) {
            // Budget exhausted, roll back
            if (remaining != null) {
                redisTemplate.opsForValue().increment(budgetKey, cost.longValue());
            }
            log.info("Budget allocation FAILED for case {}: insufficient budget", caseId);
            return false;
        }

        // Mark this case as allocated
        redisTemplate.opsForValue().set(allocationKey, cost.toString());

        // Set expiry for both keys at midnight UTC
        long ttl = secondsUntilMidnightUTC();
        redisTemplate.expire(budgetKey, ttl, TimeUnit.SECONDS);
        redisTemplate.expire(allocationKey, ttl, TimeUnit.SECONDS);

        log.info("Budget allocated for case {}: cost={}, remaining={}", caseId, cost, remaining);
        return true;
    }

    /**
     * Get remaining budget for a merchant.
     */
    public BigDecimal getRemainingBudget(UUID merchantId) {
        String budgetKey = budgetKey(merchantId);
        String value = redisTemplate.opsForValue().get(budgetKey);

        if (value == null) {
            // Initialize budget if not exists
            initializeBudget(merchantId);
            return DEFAULT_DAILY_BUDGET;
        }

        return new BigDecimal(value);
    }

    /**
     * Get consumed budget for a merchant.
     */
    public BigDecimal getConsumedBudget(UUID merchantId) {
        BigDecimal remaining = getRemainingBudget(merchantId);
        return DEFAULT_DAILY_BUDGET.subtract(remaining);
    }

    /**
     * Initialize daily budget for a merchant.
     */
    public void initializeBudget(UUID merchantId) {
        String budgetKey = budgetKey(merchantId);
        redisTemplate.opsForValue().set(budgetKey, DEFAULT_DAILY_BUDGET.toString());

        long ttl = secondsUntilMidnightUTC();
        redisTemplate.expire(budgetKey, ttl, TimeUnit.SECONDS);

        log.info("Initialized budget for merchant {}: {}", merchantId, DEFAULT_DAILY_BUDGET);
    }

    /**
     * Rank and allocate budget to eligible recovery opportunities.
     * Returns list of case IDs that received budget allocation.
     */
    public List<UUID> allocateBudgetToTopOpportunities(UUID merchantId,
                                                        List<RecoveryOpportunity> opportunities) {
        // Sort by expected net value descending
        List<RecoveryOpportunity> ranked = opportunities.stream()
            .sorted(Comparator.comparing(RecoveryOpportunity::getExpectedNetValue).reversed())
            .collect(Collectors.toList());

        List<UUID> allocated = new ArrayList<>();

        for (RecoveryOpportunity opp : ranked) {
            boolean success = allocateBudget(merchantId, opp.getCaseId(), opp.getActionCost());
            if (success) {
                allocated.add(opp.getCaseId());
            } else {
                log.info("Budget exhausted. Remaining {} opportunities not funded.",
                    ranked.size() - allocated.size());
                break;
            }
        }

        return allocated;
    }

    private String budgetKey(UUID merchantId) {
        String today = LocalDate.now(ZoneOffset.UTC).toString();
        return String.format("budget:merchant:%s:%s", merchantId, today);
    }

    private String allocationKey(UUID merchantId, UUID caseId) {
        String today = LocalDate.now(ZoneOffset.UTC).toString();
        return String.format("budget:allocation:%s:%s:%s", merchantId, today, caseId);
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
