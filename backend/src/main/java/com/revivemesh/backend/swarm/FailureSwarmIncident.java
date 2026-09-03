package com.revivemesh.backend.swarm;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "failure_swarm_incidents")
public class FailureSwarmIncident {

    @Id
    private String swarmId;
    private String fingerprint;
    private int affectedTransactionCount;
    private double failureRate;
    private double baselineFailureRate;
    private double confidence;
    private String affectedDimensions; // JSON or comma separated string
    private boolean active;
    private Instant detectedAt;

    public FailureSwarmIncident() {}

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
    public String getAffectedDimensions() { return affectedDimensions; }
    public void setAffectedDimensions(String affectedDimensions) { this.affectedDimensions = affectedDimensions; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Instant getDetectedAt() { return detectedAt; }
    public void setDetectedAt(Instant detectedAt) { this.detectedAt = detectedAt; }
}
