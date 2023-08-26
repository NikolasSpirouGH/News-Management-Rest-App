package news.controller;

import news.entity.Article;
import news.entity.ArticleStatus;
import news.service.ArticleService;
import news.dto.CommentRequest;
import news.entity.Comment;
import news.entity.CommentStatus;
import news.service.CommentService;
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

    @PostMapping("/createComment")
    public ResponseEntity<String> createComment(@Valid @RequestBody CommentRequest request) throws IllegalAccessException {
        Long articleId = request.getArticleId();
        Article findArticle =  articleService.getArticleById(articleId);

        Comment comment = new Comment();
        comment.setText(request.getText());
        comment.setAuthorName(request.getAuthorName());
        comment.setStatus(CommentStatus.CREATED);
        comment.setArticle(findArticle);

        if(findArticle.getStatus() != ArticleStatus.PUBLISHED) {
            throw new RuntimeException("Not published article");
        }

        commentService.createComment(comment);

        return new ResponseEntity<>("Comment Saved : " + comment.getText() + " " + " with id " + " " + comment.getCommentId() + " " + "for article " + findArticle.getName(), HttpStatus.CREATED);
    }

//    @PutMapping("/updateComment/{commentId}")
//    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId,@Valid @RequestBody CommentRequest request) {
//
//        Comment existingComment = commentService.getCommentById(commentId);
//
//        if (existingComment == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        if (existingComment.getStatus() == CommentStatus.APPROVED) {
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//
//        existingComment.setText(request.getText());
//
//        CommentResponse commentResponse = new CommentResponse(existingComment.getArticle().getName(),existingComment.getText(),existingComment.getStatus());
//
//        return new ResponseEntity<>(commentResponse ,HttpStatus.CREATED);
//    }
//
//    @PutMapping("/acceptComment/{articleId}/{commentId}")
//    public ResponseEntity<CommentResponse> acceptComment(@PathVariable Long articleId, @PathVariable Long commentId){
//        Comment comment = commentService.getCommentById(commentId);
//
//        if (comment == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//
//        comment.setStatus(CommentStatus.APPROVED);
//
//        CommentResponse commentResponse = new CommentResponse(comment.getArticle().getName(),comment.getText(),comment.getStatus());
//
//        return new ResponseEntity<>(commentResponse, HttpStatus.OK);
//    }

}



