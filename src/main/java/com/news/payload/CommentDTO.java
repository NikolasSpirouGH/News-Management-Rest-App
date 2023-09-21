package com.news.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CommentDTO {

    private Long articleId;
    @NotBlank
    private String text;
    private LocalDate createdDate;
    private String username;
}
