package com.revivemesh.backend.policy;

public class PolicyDecision {
    private final boolean allowed;
    private final String reason;
    private final String policy;

    private PolicyDecision(boolean allowed, String reason, String policy) {
        this.allowed = allowed;
        this.reason = reason;
        this.policy = policy;
    }

    public static PolicyDecision allow() {
        return new PolicyDecision(true, null, null);
    }

    public static PolicyDecision block(String policy, String reason) {
        return new PolicyDecision(false, reason, policy);
    }

    public boolean isAllowed() {
        return allowed;
    }

    public String getReason() {
        return reason;
    }

    public String getPolicy() {
        return policy;
    }
}
