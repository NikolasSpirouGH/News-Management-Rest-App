package com.news.controller;

import lombok.RequiredArgsConstructor;
import com.news.payload.CommentDTO;
import com.news.service.CommentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/createComment/{articleId}")
//    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN', 'VISITOR', 'JOURNALIST')")
    public ResponseEntity<CommentDTO> createComment(@PathVariable(value = "articleId") long articleId,
                                                    @Valid @RequestBody CommentDTO commentDto, @AuthenticationPrincipal UserDetails userDetails){
        return new ResponseEntity<>(commentService.createComment(articleId, commentDto, userDetails), HttpStatus.CREATED);
    }

    @PutMapping("/comments/updateComment/{id}")
    @PreAuthorize("hasAnyAuthority('EDITOR', 'ADMIN')")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(value = "id") Long commentId,
                                                    @Valid @RequestBody CommentDTO commentDto){
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDto);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @PutMapping("/approveComment/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public ResponseEntity<String> approveComment(@PathVariable(value = "id") Long commentId){
        CommentDTO updatedComment = commentService.approveComment(commentId);
        return new ResponseEntity<>("Comment Approved with id" + " " +  commentId, HttpStatus.OK);
    }

    @PutMapping("/rejectComment/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public ResponseEntity<String> rejectComment(@PathVariable(value = "id") Long commentId){
        return commentService.rejectComment(commentId);
    }
    @GetMapping("/getCommentsByArticleId/{id}")
    public ResponseEntity<List<CommentDTO>> getCommentsByArticleId(@PathVariable(value = "id")Long articleId, @AuthenticationPrincipal UserDetails userDetails){
        System.out.println("GDGDS");
        return ResponseEntity.ok(commentService.getCommentsByArticleId(articleId,  userDetails));
    }

    @GetMapping("/getCommentById/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public CommentDTO getCommentById(@PathVariable(value = "id") Long commentId){
        return commentService.getCommentById(commentId);
    }

}



