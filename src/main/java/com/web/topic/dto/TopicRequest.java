package com.web.topic.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopicRequest {

    @NotBlank(message = "Name should not be null")
    private String name;

    private String fathersName;
}
