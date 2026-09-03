package com.revivemesh.backend.swarm;

import com.revivemesh.backend.config.KafkaTopicConfig;
import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.EventProducer;
import com.revivemesh.backend.event.FailureSwarmDetectedEvent;
import com.revivemesh.backend.event.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class FailureSwarmService {
    private static final Logger log = LoggerFactory.getLogger(FailureSwarmService.class);

    private final FailureSwarmIncidentRepository repository;
    private final EventProducer eventProducer;

    @Value("${revive.swarm.threshold:5}")
    private int swarmThreshold;

    @Value("${revive.swarm.window-seconds:60}")
    private int windowSeconds;

    // MVP: In-memory aggregation grouped by fingerprint (processor:issuer)
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<Instant>> failureWindows = new ConcurrentHashMap<>();

    public FailureSwarmService(FailureSwarmIncidentRepository repository, EventProducer eventProducer) {
        this.repository = repository;
        this.eventProducer = eventProducer;
    }

    @Transactional
    public void processFailureEvent(EventEnvelope<PaymentFailedEvent> envelope) {
        PaymentFailedEvent event = envelope.getPayload();
        if (event == null || event.getProcessor() == null || event.getIssuer() == null) {
            return;
        }

        String fingerprint = event.getProcessor() + ":" + event.getIssuer();
        Instant now = Instant.now();

        failureWindows.computeIfAbsent(fingerprint, k -> new CopyOnWriteArrayList<>()).add(now);

        List<Instant> timestamps = failureWindows.get(fingerprint);
        
        // Clean up old entries
        Instant cutoff = now.minusSeconds(windowSeconds);
        timestamps.removeIf(t -> t.isBefore(cutoff));

        int recentFailures = timestamps.size();

        if (recentFailures >= swarmThreshold) {
            detectSwarm(fingerprint, recentFailures);
            // Clear to avoid continuous triggers for the exact same window, or rely on active incidents
            timestamps.clear(); 
        }
    }

    private void detectSwarm(String fingerprint, int failureCount) {
        // Check if an active swarm already exists for this fingerprint (MVP simplistic approach)
        boolean alreadyActive = repository.findByActiveTrue().stream()
                .anyMatch(incident -> fingerprint.equals(incident.getFingerprint()));

        if (alreadyActive) {
            log.debug("Swarm already active for fingerprint: {}", fingerprint);
            return;
        }

        log.warn("Failure Swarm Detected! Fingerprint: {}, Failures: {}", fingerprint, failureCount);

        FailureSwarmIncident incident = new FailureSwarmIncident();
        incident.setSwarmId(UUID.randomUUID().toString());
        incident.setFingerprint(fingerprint);
        incident.setAffectedTransactionCount(failureCount);
        // MVP simplistic rates
        incident.setFailureRate(1.0); 
        incident.setBaselineFailureRate(0.01);
        incident.setConfidence(0.95);
        incident.setAffectedDimensions("processor,issuer");
        incident.setActive(true);
        incident.setDetectedAt(Instant.now());

        repository.save(incident);

        FailureSwarmDetectedEvent detectedEvent = new FailureSwarmDetectedEvent();
        detectedEvent.setSwarmId(incident.getSwarmId());
        detectedEvent.setFingerprint(incident.getFingerprint());
        detectedEvent.setAffectedTransactionCount(incident.getAffectedTransactionCount());
        detectedEvent.setFailureRate(incident.getFailureRate());
        detectedEvent.setBaselineFailureRate(incident.getBaselineFailureRate());
        detectedEvent.setConfidence(incident.getConfidence());
        detectedEvent.setAffectedDimensions(List.of("processor", "issuer"));
        detectedEvent.setSuppressionRecommended(true);

        EventEnvelope<FailureSwarmDetectedEvent> envelope = new EventEnvelope<>();
        envelope.setEventId(UUID.randomUUID().toString());
        envelope.setEventType("FailureSwarmDetectedEvent");
        envelope.setOccurredAt(Instant.now());
        envelope.setSchemaVersion("1.0");
        envelope.setPayload(detectedEvent);

        eventProducer.sendEvent(KafkaTopicConfig.FAILURE_CLUSTERS_TOPIC, incident.getFingerprint(), envelope);
    }
}
