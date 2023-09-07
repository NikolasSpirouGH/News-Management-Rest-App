package news.service;

import news.entity.Comment;
import news.payload.CommentDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentDTO createComment(long articleId, CommentDTO request);

    List<Comment> getCommentsByArticleId(Long articleId);

    Comment getCommentById(Long commentId);
}
