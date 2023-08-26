package news.service;

import news.entity.Comment;
import news.repository.CommentRepository;
import news.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    CommentRepository commentRepository;

    @Override
    public Comment createComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> getCommentsByArticleId(Long articleId) {
        return commentRepository.findByArticleArticleId(articleId);
    }

    @Override
    public Comment getCommentById(Long commentId) {
        return commentRepository.findByCommentId(commentId);
    }
}
