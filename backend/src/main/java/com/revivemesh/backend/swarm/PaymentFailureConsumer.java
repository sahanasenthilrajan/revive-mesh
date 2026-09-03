package com.revivemesh.backend.swarm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revivemesh.backend.config.KafkaTopicConfig;
import com.revivemesh.backend.event.EventEnvelope;
import com.revivemesh.backend.event.PaymentFailedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentFailureConsumer {
    private static final Logger log = LoggerFactory.getLogger(PaymentFailureConsumer.class);

    private final ObjectMapper objectMapper;
    private final FailureSwarmService failureSwarmService;

    public PaymentFailureConsumer(ObjectMapper objectMapper, FailureSwarmService failureSwarmService) {
        this.objectMapper = objectMapper;
        this.failureSwarmService = failureSwarmService;
    }

    @KafkaListener(topics = KafkaTopicConfig.PAYMENT_FAILURES_TOPIC, groupId = "swarm-detection-group")
    public void consume(String message) {
        try {
            EventEnvelope<PaymentFailedEvent> event = objectMapper.readValue(message, new TypeReference<>() {});
            log.debug("Consumed payment failure event: {}", event.getEventId());
            failureSwarmService.processFailureEvent(event);
        } catch (Exception e) {
            log.error("Failed to process payment failure message", e);
        }
    }
}
