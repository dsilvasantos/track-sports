package com.example.livesportstracker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TopicService {
    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);


    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000;

    public  void safeSend(KafkaTemplate<String, String> kafkaTemplate, String topic, String message) {
        int attempt = 0;
        while (attempt < MAX_RETRIES) {
            try {
                kafkaTemplate.send(topic, message).get();
                logger.info("Message sent to Kafka topic {}: {}", topic, message);
                return;
            } catch (Exception e) {
                attempt++;
                logger.warn("Attempt {} failed to send to Kafka topic {}: {}", attempt, topic, e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    logger.error("Exceeded max retries for sending to Kafka topic {}. Message dropped: {}", topic, message, e);
                } else {
                    try {
                        Thread.sleep(RETRY_DELAY_MS);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        logger.error("Retry sleep interrupted while sending to Kafka topic {}", topic);
                        return;
                    }
                }
            }
        }
    }
}
