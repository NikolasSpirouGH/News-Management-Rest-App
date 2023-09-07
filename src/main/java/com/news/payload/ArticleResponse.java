package com.news.payload;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Data
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor

public class ArticleResponse {

    private Long articleId;

    private String articleName;

    private String articleContent;

    private List<TopicDTO> topics;

    private LocalDate createdDate;

    private List<CommentDTO> comments;

}
