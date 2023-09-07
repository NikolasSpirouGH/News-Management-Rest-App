package news.controller;

import lombok.RequiredArgsConstructor;
import news.payload.CommentDTO;
import news.service.ArticleService;
import news.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private final ArticleService articleService;

    @PostMapping("/createComment/{articleId}")
    public ResponseEntity<CommentDTO> createComment(@PathVariable(value = "articleId") long articleId,
                                                    @Valid @RequestBody CommentDTO commentDto){
        return new ResponseEntity<>(commentService.createComment(articleId, commentDto), HttpStatus.CREATED);
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



