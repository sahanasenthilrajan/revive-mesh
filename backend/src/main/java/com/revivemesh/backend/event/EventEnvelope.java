package com.revivemesh.backend.event;

import java.time.Instant;
import java.util.UUID;

public class EventEnvelope<T> {
    private String eventId;
    private String eventType;
    private Instant occurredAt;
    private String schemaVersion;
    private String aggregateId;
    private String merchantId;
    private T payload;

    public EventEnvelope() {}

    public EventEnvelope(UUID eventId, String eventType, Instant occurredAt, String schemaVersion,
                         UUID aggregateId, UUID merchantId, T payload) {
        this.eventId = eventId.toString();
        this.eventType = eventType;
        this.occurredAt = occurredAt;
        this.schemaVersion = schemaVersion;
        this.aggregateId = aggregateId != null ? aggregateId.toString() : null;
        this.merchantId = merchantId != null ? merchantId.toString() : null;
        this.payload = payload;
    }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public Instant getOccurredAt() { return occurredAt; }
    public void setOccurredAt(Instant occurredAt) { this.occurredAt = occurredAt; }
    public String getSchemaVersion() { return schemaVersion; }
    public void setSchemaVersion(String schemaVersion) { this.schemaVersion = schemaVersion; }
    public String getAggregateId() { return aggregateId; }
    public void setAggregateId(String aggregateId) { this.aggregateId = aggregateId; }
    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }
    public T getPayload() { return payload; }
    public void setPayload(T payload) { this.payload = payload; }
}
