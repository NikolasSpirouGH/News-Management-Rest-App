package com.news.repository;

import com.news.entity.Article;
import com.news.entity.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{

    List<Article> findByNameContainingAndContentContaining(String name, String content);

    @Query("SELECT a FROM Article a WHERE a.user.id = :userId AND a.status != :articleStatus")
    List<Article> findByIdAAndNotStatus(Long userId, ArticleStatus articleStatus);

    List<Article> findAllByStatus(ArticleStatus articleStatus);
    Article findByName(String name);

    List<Article> findByContentContaining(String content);

    List<Article> findByStatusOrderByStatusDescCreatedAtDesc(ArticleStatus status);

    List<Article> findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Article> findAllByOrderByStatusDescCreatedAtDesc();

    List<Article> findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(ArticleStatus status, LocalDateTime localDateTime, LocalDateTime localDateTime1, ArticleStatus status1, LocalDateTime localDateTime2, LocalDateTime localDateTime3);

    Article findByNameContaining(String name);

    List<Article> findByUserIdAndStatusAndCreatedAtBetweenOrUserIdAndStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(Long journalistUserId, ArticleStatus status, LocalDateTime localDateTime, LocalDateTime localDateTime1, Long journalistUserId1, ArticleStatus status1, LocalDateTime localDateTime2, LocalDateTime localDateTime3);

    List<Article> findByUserIdAndStatusOrderByStatusDescCreatedAtDesc(Long journalistUserId, ArticleStatus status);

    List<Article> findByUserIdAndCreatedAtBetweenOrderByStatusDescCreatedAtDesc(Long journalistUserId, LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Article> findByUserIdOrderByStatusDescCreatedAtDesc(Long journalistUserId);

    List<Article> findAllByStatusOrderByStatusDescCreatedAtDesc(ArticleStatus articleStatus);

    List<Article> findByNameContainingAndContentContainingAndUserId(String name, String content, Long userId);

    List<Article> findByNameContainingAndUserId(String name, Long userId);

    List<Article> findByContentContainingAndUserId(String content, Long userId);

    List<Article> findByUserId(Long userId);
}
