package com.news.payload;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RejectArticleRequest {

    @NotBlank
    private String username;

    @NotBlank
    private String rejectionReason;
}
