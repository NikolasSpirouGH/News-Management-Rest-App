package news.news_management.comment.service;

import news.news_management.article.model.Article;
import news.news_management.comment.model.Comment;
import news.news_management.comment.repository.CommentRepository;
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
}
