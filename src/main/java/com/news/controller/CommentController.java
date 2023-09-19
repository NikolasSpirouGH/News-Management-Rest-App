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

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    @PostMapping("/createComment/{articleId}")
    public ResponseEntity<CommentDTO> createComment(@PathVariable(value = "articleId") long articleId,
                                                    @Valid @RequestBody CommentDTO commentDto, @AuthenticationPrincipal UserDetails userDetails){
        return new ResponseEntity<>(commentService.createComment(articleId, commentDto, userDetails), HttpStatus.CREATED);
    }

    @PutMapping("/comments/updateComment/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public ResponseEntity<CommentDTO> updateComment(@PathVariable(value = "id") Long commentId,
                                                    @Valid @RequestBody CommentDTO commentDto, @AuthenticationPrincipal UserDetails userDetails){
        CommentDTO updatedComment = commentService.updateComment(commentId, commentDto);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @PutMapping("/comments/approveComment/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public ResponseEntity<CommentDTO> approveComment(@PathVariable(value = "id") Long commentId,
                                                     @Valid @RequestBody CommentDTO commentDto){
        CommentDTO updatedComment = commentService.approveComment(commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    @PutMapping("/comments/rejectComment/{id}")
    @PreAuthorize("hasAnyAuthority( 'EDITOR', 'ADMIN')")
    public ResponseEntity<CommentDTO> rejectComment(@PathVariable(value = "id") Long commentId,
                                                    @Valid @RequestBody CommentDTO commentDto){
        CommentDTO updatedComment = commentService.rejectComment(commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }
}



