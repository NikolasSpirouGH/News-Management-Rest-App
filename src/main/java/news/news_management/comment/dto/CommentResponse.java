package news.news_management.comment.dto;

import news.news_management.comment.model.CommentStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentResponse {

    private String articleName;

    private String text;

    private CommentStatus status;
}
