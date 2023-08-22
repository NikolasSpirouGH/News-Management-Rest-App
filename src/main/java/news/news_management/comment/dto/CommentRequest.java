package news.news_management.comment.dto;

import lombok.Data;

@Data
public class CommentRequest {

    private Long articleId;
    private String text;
    private String authorName;


}
