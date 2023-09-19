package com.news.service;

import com.news.payload.CommentDTO;
import com.news.entity.Comment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentDTO createComment(long articleId, CommentDTO request, UserDetails userDetails);

    CommentDTO updateComment(long commentId, CommentDTO commentRequest);

    CommentDTO approveComment(long commentId);
    CommentDTO rejectComment(long commentId);

    List<Comment> getCommentsByArticleId(Long articleId);

    Comment getCommentById(Long commentId);
}
