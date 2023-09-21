package com.news.service;

import com.news.entity.ArticleStatus;
import com.news.payload.ArticleDTO;
import com.news.payload.CommentDTO;
import com.news.entity.Comment;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public interface CommentService {
    CommentDTO createComment(long articleId, CommentDTO request, UserDetails userDetails);

    CommentDTO updateComment(long commentId, CommentDTO commentRequest);

    CommentDTO approveComment(long commentId);
    ResponseEntity<String> rejectComment(long commentId);

    List<CommentDTO> getCommentsByArticleId(Long articleId, UserDetails userDetails);

    CommentDTO getCommentById(Long commentId);
}

