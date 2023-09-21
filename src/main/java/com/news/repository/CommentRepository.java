package com.news.repository;

import com.news.entity.Comment;
import com.news.entity.CommentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByArticleArticleId(Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.article.articleId = :articleId AND c.status = :commentStatus")
    List<Comment> findAllByArticleIdAndStatus(Long articleId, CommentStatus commentStatus);
    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId AND c.article.articleId = :articleId")
    List<Comment> findAllByUserIdAndArticleId(Long userId, Long articleId);

    @Query("SELECT c FROM Comment c WHERE c.user.id = :userId")
    List<Comment> findAllByUserId(Long userId);

    Comment findByCommentId(Long commentId);
}
