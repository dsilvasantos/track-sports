package com.example.livesportstracker.service;

import com.example.livesportstracker.interfaces.client.EventClient;
import com.example.livesportstracker.model.EventStatusRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import static com.example.livesportstracker.model.enums.EnumStatus.LIVE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);

    @Autowired
    private EventClient eventClient;

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;


    private final Map<String, Boolean> liveEvents = new ConcurrentHashMap<>();

    public EventService(KafkaTemplate<String, String> kafkaTemplate,
                        @Value("${tracker.topic}") String topic) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;

    }

    public void updateEventStatus(EventStatusRequest request) {
        if (LIVE == request.getStatus()) {
            liveEvents.put(request.getEventId(), true);
        } else {
            liveEvents.remove(request.getEventId());
        }
        logger.info("Updated status of event {}: {}", request.getEventId(), request.getStatus());
    }

    @CircuitBreaker(name = "eventClient", fallbackMethod = "getEventFallback")
    @Retry(name = "eventClient")
    public String safeGet(String eventId) {
        return eventClient.getEvent(eventId);
    }

    public String getEventFallback(String eventId, Throwable t) {
        logger.error("Fallback triggered for eventId {} due to: {}", eventId, t.getMessage());
        return null;
    }

    @Scheduled(fixedRateString = "${tracker.polling-interval}000")
    public void pollLiveEvents() {
        for (String eventId : liveEvents.keySet()) {
            try {
                String response = safeGet(eventId);
                kafkaTemplate.send(topic, response);
                logger.info("Published event {} update: {}", eventId, response);
            } catch (Exception e) {
                logger.error("Failed to process event " + eventId, e);
            }
        }
    }
}