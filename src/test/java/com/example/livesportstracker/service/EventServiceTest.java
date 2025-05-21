package com.example.livesportstracker.service;

import com.example.livesportstracker.interfaces.client.EventClient;
import com.example.livesportstracker.model.EventStatusRequest;
import com.example.livesportstracker.model.enums.EnumStatus;
import org.awaitility.reflect.WhiteboxImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Map;

import static org.mockito.Mockito.*;

public class EventServiceTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private EventClient eventClient;

    @Mock
    private TopicService topicService;

    @InjectMocks
    private EventService eventService;

    private final String topic = "test-topic";

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(kafkaTemplate, topic, topicService,eventClient);
    }

    @Test
    void testUpdateEventStatus_LiveEvent() {
        EventStatusRequest request = new EventStatusRequest("event123", EnumStatus.LIVE);
        eventService.updateEventStatus(request);

        when(eventClient.getEvent("event123")).thenReturn("mock-response");

        eventService.pollLiveEvents();

        verify(topicService).safeSend(kafkaTemplate, topic, "mock-response");
    }

    @Test
    void testUpdateEventStatus_NonLiveEvent() {
        EventStatusRequest request = new EventStatusRequest("event123", EnumStatus.NOT_LIVE);
        eventService.updateEventStatus(request);

        eventService.pollLiveEvents();

        verifyNoInteractions(eventClient);
        verifyNoInteractions(topicService);
    }

    @Test
    void testPollLiveEvents_successful() {
        EventStatusRequest request = new EventStatusRequest("e1", EnumStatus.LIVE);
        eventService.updateEventStatus(request);

        when(eventClient.getEvent("e1")).thenReturn("{ \"status\": \"live\" }");

        eventService.pollLiveEvents();

        verify(topicService).safeSend(eq(kafkaTemplate), eq(topic), eq("{ \"status\": \"live\" }"));
    }

    @Test
    void testPollLiveEvents_withException() {
        EventStatusRequest request = new EventStatusRequest("e1", EnumStatus.LIVE);
        eventService.updateEventStatus(request);

        when(eventClient.getEvent("e1")).thenThrow(new RuntimeException("API down"));

        eventService.pollLiveEvents();


        verify(topicService, never()).safeSend(any(), any(), any());
    }
}