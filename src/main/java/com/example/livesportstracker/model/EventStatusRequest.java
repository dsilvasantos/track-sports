package com.example.livesportstracker.model;

import com.example.livesportstracker.annotation.EnumValid;
import com.example.livesportstracker.model.enums.EnumStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.io.Serializable;


public class EventStatusRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "ID of sport event", example = "1234")
    @NotBlank(message = "Event ID cannot be blank")
    private String eventId;

    @Schema(description = "Status of event: 'LIVE' or 'NOT_LIVE'", example = "LIVE")
    @NotBlank(message = "Status cannot be blank")
    @EnumValid(enumClass = EnumStatus.class, message = "Invalid status")
    private EnumStatus status;

    public EventStatusRequest(String eventId, EnumStatus status) {
        this.eventId = eventId;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public EnumStatus getStatus() {
        return status;
    }

    public void setStatus(EnumStatus status) {
        this.status = status;
    }
}