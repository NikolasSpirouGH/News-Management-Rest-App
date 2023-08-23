package news.news_management.article.dto;

import lombok.*;
import news.news_management.article.model.Article;
import news.news_management.comment.model.Comment;

import java.util.List;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString

public class ArticleResponseWithComments {

    private Article article;

    private List<Comment> comments;


}
