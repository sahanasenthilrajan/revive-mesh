package com.revivemesh.backend.swarm;

import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.EventProducer;
import com.revivemesh.backend.event.PaymentFailedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Transactional
public class FailureSwarmServiceTest {

    @Autowired
    private FailureSwarmService failureSwarmService;

    @Autowired
    private FailureSwarmIncidentRepository repository;

    @MockBean
    private EventProducer eventProducer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void testNormalTrafficDoesNotTriggerSwarm() {
        // Send 3 events (below threshold of 5)
        for (int i = 0; i < 3; i++) {
            failureSwarmService.processFailureEvent(createEvent("Stripe", "Chase"));
        }

        assertThat(repository.findByActiveTrue()).isEmpty();
        verify(eventProducer, times(0)).sendEvent(any(), any(), any());
    }

    @Test
    void testConcentratedAbnormalTrafficTriggersSwarm() {
        // Send 5 events (threshold is 5)
        for (int i = 0; i < 5; i++) {
            failureSwarmService.processFailureEvent(createEvent("Stripe", "Chase"));
        }

        var incidents = repository.findByActiveTrue();
        assertThat(incidents).hasSize(1);
        assertThat(incidents.get(0).getFingerprint()).isEqualTo("Stripe:Chase");

        verify(eventProducer, times(1)).sendEvent(eq("failure-clusters"), eq("Stripe:Chase"), any());
    }

    @Test
    void testUnrelatedFailuresAreNotIncorrectlyMerged() {
        // Send 3 events for Stripe:Chase
        for (int i = 0; i < 3; i++) {
            failureSwarmService.processFailureEvent(createEvent("Stripe", "Chase"));
        }
        // Send 3 events for PayPal:BoA
        for (int i = 0; i < 3; i++) {
            failureSwarmService.processFailureEvent(createEvent("PayPal", "BoA"));
        }

        // Neither should reach threshold of 5
        assertThat(repository.findByActiveTrue()).isEmpty();
    }
    
    @Test
    void testIncidentPersistence() {
        for (int i = 0; i < 5; i++) {
            failureSwarmService.processFailureEvent(createEvent("Adyen", "Citi"));
        }

        var incidents = repository.findByActiveTrue();
        assertThat(incidents).hasSize(1);
        var incident = incidents.get(0);
        assertThat(incident.getAffectedTransactionCount()).isEqualTo(5);
        assertThat(incident.isActive()).isTrue();
        assertThat(incident.getDetectedAt()).isNotNull();
    }

    private EventEnvelope<PaymentFailedEvent> createEvent(String processor, String issuer) {
        PaymentFailedEvent payload = new PaymentFailedEvent();
        payload.setProcessor(processor);
        payload.setIssuer(issuer);
        
        EventEnvelope<PaymentFailedEvent> env = new EventEnvelope<>();
        env.setEventId(UUID.randomUUID().toString());
        env.setPayload(payload);
        env.setOccurredAt(Instant.now());
        return env;
    }
}
