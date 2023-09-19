package com.news.service;

import com.news.payload.CommentDTO;
import com.news.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CommentService {
    CommentDTO createComment(long articleId, CommentDTO request);

    CommentDTO updateComment(Long articleId, long commentId, CommentDTO commentRequest);

    List<Comment> getCommentsByArticleId(Long articleId);

    Comment getCommentById(Long commentId);
}
