package com.example.livesportstracker.integration;

import com.example.livesportstracker.service.EventService;
import com.example.livesportstracker.service.TopicService;
import com.example.livesportstracker.interfaces.client.EventClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class EventServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventService eventService;

    @MockBean
    private EventClient eventClient;

    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;

    @MockBean
    private TopicService topicService;


    @Test
    void givenLiveEvent_whenStatusIsUpdated_thenEventIsPolledAndPublished() throws Exception {

        mockMvc.perform(
                        post("/events/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                             {
                               "eventId": "match-42",
                               "status": "LIVE"
                             }
                             """)
                )
                .andExpect(status().isOk());

        when(eventClient.getEvent("match-42"))
                .thenReturn("{\"eventId\":\"match-42\",\"score\":\"1-0\"}");

        eventService.pollLiveEvents();

        verify(eventClient, times(1)).getEvent("match-42");
        verify(topicService).safeSend(
                eq(kafkaTemplate),
                eq("live-events"),               // valor default em application.yml
                eq("{\"eventId\":\"match-42\",\"score\":\"1-0\"}")
        );
        verifyNoMoreInteractions(topicService);
    }


    @Test
    void givenFeignFailure_whenPolling_thenNoKafkaSend() throws Exception {

        mockMvc.perform(
                        post("/events/status")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                             {"eventId":"match-404","status":"LIVE"}
                             """)
                )
                .andExpect(status().isOk());

        when(eventClient.getEvent("match-404"))
                .thenThrow(new RuntimeException("API unavailable "));

        eventService.pollLiveEvents();

        verify(eventClient).getEvent("match-404");
    }
}
