package id.ac.ui.cs.advprog.yomubackend.achievements.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.UUID;

@Data
public class EventTriggerRequest {
    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("eventType")
    private String eventType;
}