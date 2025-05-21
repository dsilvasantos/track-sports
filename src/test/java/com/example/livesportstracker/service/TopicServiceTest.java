package com.example.livesportstracker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;

public class TopicServiceTest {

    private KafkaTemplate<String, String> kafkaTemplate;
    private TopicService topicService;

    @BeforeEach
    void setup() {
        kafkaTemplate = mock(KafkaTemplate.class);
        topicService = new TopicService();
    }

    @Test
    void testSafeSend_SuccessfulOnFirstAttempt() throws Exception {
        // Simula envio com sucesso
        CompletableFuture future = CompletableFuture.completedFuture(null);
        when(kafkaTemplate.send("topic1", "message1")).thenReturn(future);

        topicService.safeSend(kafkaTemplate, "topic1", "message1");

        verify(kafkaTemplate, times(1)).send("topic1", "message1");
    }

    @Test
    void testSafeSend_SucceedsAfterRetry() throws Exception {
        // Simula falha na primeira e sucesso na segunda tentativa
        CompletableFuture failed = new CompletableFuture();
        failed.completeExceptionally(new RuntimeException("Kafka unavailable"));

        CompletableFuture success = CompletableFuture.completedFuture(null);

        when(kafkaTemplate.send("topic1", "message1"))
                .thenReturn(failed)   // 1ª tentativa falha
                .thenReturn(success); // 2ª tentativa tem sucesso

        topicService.safeSend(kafkaTemplate, "topic1", "message1");

        verify(kafkaTemplate, times(2)).send("topic1", "message1");
    }

    @Test
    void testSafeSend_AllRetriesFail() {
        // Simula falhas em todas as tentativas
        CompletableFuture failed = new CompletableFuture();
        failed.completeExceptionally(new RuntimeException("Kafka down"));

        when(kafkaTemplate.send("topic1", "message1"))
                .thenReturn(failed); // falha sempre

        topicService.safeSend(kafkaTemplate, "topic1", "message1");

        verify(kafkaTemplate, times(3)).send("topic1", "message1");
    }
}