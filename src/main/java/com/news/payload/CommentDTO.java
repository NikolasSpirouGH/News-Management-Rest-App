package com.news.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentDTO {

    private Long commentId;
    private Long articleId;
    @NotBlank
    private String text;
    private String username;
}
