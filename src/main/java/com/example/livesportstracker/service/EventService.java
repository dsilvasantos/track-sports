package com.example.livesportstracker.service;

import com.example.livesportstracker.interfaces.client.EventClient;
import com.example.livesportstracker.model.EventStatusRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import static com.example.livesportstracker.model.enums.EnumStatus.LIVE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class EventService {
    private static final Logger logger = LoggerFactory.getLogger(EventService.class);


    private final EventClient eventClient;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String topic;
    private final TopicService topicService;

    private final Map<String, Boolean> liveEvents = new ConcurrentHashMap<>();

    public EventService(KafkaTemplate<String, String> kafkaTemplate,
                        @Value("${tracker.topic}") String topic,TopicService topicService,EventClient eventClient) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
        this.topicService = topicService;
        this.eventClient = eventClient;
    }

    public void updateEventStatus(EventStatusRequest request) {
        if (LIVE == request.getStatus()) {
            liveEvents.put(request.getEventId(), true);
        } else {
            liveEvents.remove(request.getEventId());
        }
        logger.info("Updated status of event {}: {}", request.getEventId(), request.getStatus());
    }

    @Scheduled(fixedRateString = "${tracker.polling-interval}000")
    public void pollLiveEvents() {
        for (String eventId : liveEvents.keySet()) {
            try {
                String response = eventClient.getEvent(eventId);
                topicService.safeSend(kafkaTemplate, topic, response);
            } catch (Exception e) {
                logger.error("Failed to process event " + eventId, e);
            }
        }
    }
}