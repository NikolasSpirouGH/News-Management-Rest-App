package com.news.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TopicDTO {

    private Long topicId;

    @NotBlank(message = "Name should not be null")
    private String name;

    private String parentName;
}
