package com.revivemesh.backend.recovery;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recovery_decisions")
public class RecoveryDecision {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recovery_case_id", nullable = false)
    private UUID recoveryCaseId;

    @Column(name = "recommended_action", nullable = false, length = 50)
    private String recommendedAction;

    @Column(name = "expected_net_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal expectedNetValue;

    @Column(nullable = false, precision = 5, scale = 4)
    private BigDecimal confidence;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    @Column(name = "decision_reason", columnDefinition = "TEXT")
    private String decisionReason;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public RecoveryDecision() {}

    public RecoveryDecision(UUID recoveryCaseId, String recommendedAction, BigDecimal expectedNetValue,
                            BigDecimal confidence, String modelVersion, String decisionReason) {
        this.recoveryCaseId = recoveryCaseId;
        this.recommendedAction = recommendedAction;
        this.expectedNetValue = expectedNetValue;
        this.confidence = confidence;
        this.modelVersion = modelVersion;
        this.decisionReason = decisionReason;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRecoveryCaseId() {
        return recoveryCaseId;
    }

    public void setRecoveryCaseId(UUID recoveryCaseId) {
        this.recoveryCaseId = recoveryCaseId;
    }

    public String getRecommendedAction() {
        return recommendedAction;
    }

    public void setRecommendedAction(String recommendedAction) {
        this.recommendedAction = recommendedAction;
    }

    public BigDecimal getExpectedNetValue() {
        return expectedNetValue;
    }

    public void setExpectedNetValue(BigDecimal expectedNetValue) {
        this.expectedNetValue = expectedNetValue;
    }

    public BigDecimal getConfidence() {
        return confidence;
    }

    public void setConfidence(BigDecimal confidence) {
        this.confidence = confidence;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getDecisionReason() {
        return decisionReason;
    }

    public void setDecisionReason(String decisionReason) {
        this.decisionReason = decisionReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
