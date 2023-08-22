package news.news_management.comment.service;

import news.news_management.article.model.Article;
import news.news_management.comment.model.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    Comment createComment(Comment comment);

    List<Comment> getCommentsByArticleId(Long articleId);
}
