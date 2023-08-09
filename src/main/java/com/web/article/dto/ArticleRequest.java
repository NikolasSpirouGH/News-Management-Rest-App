package com.web.article.dto;

import com.web.topic.model.Topic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data

public class ArticleRequest {

    private String name;

    private String content;

    private Set<String> topics;

}
