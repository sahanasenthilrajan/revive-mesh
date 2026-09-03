package com.revivemesh.backend.event;

import java.util.List;

public class FailureSwarmDetectedEvent {
    private String swarmId;
    private String fingerprint;
    private int affectedTransactionCount;
    private double failureRate;
    private double baselineFailureRate;
    private double confidence;
    private List<String> affectedDimensions;
    private boolean suppressionRecommended;

    public FailureSwarmDetectedEvent() {}

    public String getSwarmId() { return swarmId; }
    public void setSwarmId(String swarmId) { this.swarmId = swarmId; }
    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }
    public int getAffectedTransactionCount() { return affectedTransactionCount; }
    public void setAffectedTransactionCount(int affectedTransactionCount) { this.affectedTransactionCount = affectedTransactionCount; }
    public double getFailureRate() { return failureRate; }
    public void setFailureRate(double failureRate) { this.failureRate = failureRate; }
    public double getBaselineFailureRate() { return baselineFailureRate; }
    public void setBaselineFailureRate(double baselineFailureRate) { this.baselineFailureRate = baselineFailureRate; }
    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }
    public List<String> getAffectedDimensions() { return affectedDimensions; }
    public void setAffectedDimensions(List<String> affectedDimensions) { this.affectedDimensions = affectedDimensions; }
    public boolean isSuppressionRecommended() { return suppressionRecommended; }
    public void setSuppressionRecommended(boolean suppressionRecommended) { this.suppressionRecommended = suppressionRecommended; }
}
