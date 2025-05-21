package com.example.livesportstracker.controller;

import com.example.livesportstracker.service.EventService;
import com.example.livesportstracker.model.EventStatusRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/events")
@Tag(name = "Event Controller", description = "Manage endpoints related of events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @Operation(
            summary = "Updates the status of an event",
            description = "Allows you to update the status of a sporting event based on the information sent.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Event and status details.\n",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventStatusRequest.class)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Event status updated successfully.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(example = "{ \"message\": \"Status updated\" }")
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request. Check if the data is correct.",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error.",
                            content = @Content
                    )
            }
    )

    @PostMapping("/status")
    public ResponseEntity<String> updateEventStatus(@Valid @RequestBody EventStatusRequest request) {
        try {
            eventService.updateEventStatus(request);
            return ResponseEntity.ok("Status updated");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid request: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal server error: " + e.getMessage());
        }
    }
}