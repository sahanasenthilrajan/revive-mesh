package com.revivemesh.backend.recovery;

import java.math.BigDecimal;
import java.util.UUID;

public class RecoveryContext {
    private UUID transactionId;
    private UUID customerId;
    private UUID merchantId;
    private BigDecimal amount;
    private String paymentMethod;
    private String processor;
    private String issuer;
    private String region;
    private String failureCode;
    private Integer attemptNumber;
    private BigDecimal customerHistoricalSuccess;
    private Integer recentContactCount;
    private Boolean incidentActive;
    private String incidentFingerprint;

    public RecoveryContext() {}

    public UUID getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(UUID transactionId) {
        this.transactionId = transactionId;
    }

    public UUID getCustomerId() {
        return customerId;
    }

    public void setCustomerId(UUID customerId) {
        this.customerId = customerId;
    }

    public UUID getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(UUID merchantId) {
        this.merchantId = merchantId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getProcessor() {
        return processor;
    }

    public void setProcessor(String processor) {
        this.processor = processor;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getFailureCode() {
        return failureCode;
    }

    public void setFailureCode(String failureCode) {
        this.failureCode = failureCode;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public BigDecimal getCustomerHistoricalSuccess() {
        return customerHistoricalSuccess;
    }

    public void setCustomerHistoricalSuccess(BigDecimal customerHistoricalSuccess) {
        this.customerHistoricalSuccess = customerHistoricalSuccess;
    }

    public Integer getRecentContactCount() {
        return recentContactCount;
    }

    public void setRecentContactCount(Integer recentContactCount) {
        this.recentContactCount = recentContactCount;
    }

    public Boolean getIncidentActive() {
        return incidentActive;
    }

    public void setIncidentActive(Boolean incidentActive) {
        this.incidentActive = incidentActive;
    }

    public String getIncidentFingerprint() {
        return incidentFingerprint;
    }

    public void setIncidentFingerprint(String incidentFingerprint) {
        this.incidentFingerprint = incidentFingerprint;
    }
}
