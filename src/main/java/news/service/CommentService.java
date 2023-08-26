package news.service;

import news.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    Comment createComment(Comment comment);

    List<Comment> getCommentsByArticleId(Long articleId);

    Comment getCommentById(Long commentId);
}
