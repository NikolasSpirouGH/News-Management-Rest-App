package news.news_management.comment.controller;

import news.news_management.article.model.Article;
import news.news_management.article.model.ArticleStatus;
import news.news_management.article.service.ArticleService;
import news.news_management.comment.dto.CommentRequest;
import news.news_management.comment.model.Comment;
import news.news_management.comment.model.CommentStatus;
import news.news_management.comment.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
public class CommentController {


    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleService articleService;

    @PostMapping("/create")
    public ResponseEntity<String> createComment(@Valid @RequestBody CommentRequest request) throws IllegalAccessException {
        Long articleId = request.getArticleId();
        Article findArticle =  articleService.getArticleById(articleId);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthorName(request.getAuthorName());
        comment.setStatus(CommentStatus.PENDING);
        comment.setArticle(findArticle);

        if(findArticle.getStatus() != ArticleStatus.PUBLISHED) {
            throw new IllegalAccessException();
        }

        // Save the comment using the CommentService
        Comment savedComment = commentService.createComment(comment);

        return new ResponseEntity<>("Comment Saved : " + comment.getText() + " " + " with id " + " " + comment.getCommentId(), HttpStatus.CREATED);
    }

}



