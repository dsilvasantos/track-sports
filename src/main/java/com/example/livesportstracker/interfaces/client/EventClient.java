package com.example.livesportstracker.interfaces.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "event-client", url = "${tracker.external-url}")
public interface EventClient {

    @GetMapping
    String getEvent(@RequestParam("eventId") String eventId);
}