package com.news.service;

import com.news.entity.ArticleStatus;
import com.news.payload.ArticleDTO;
import com.news.entity.Article;
import com.news.payload.RejectArticleRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.List;

public interface ArticleService {

    ArticleDTO getArticleById(Long articleId);

    List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate);

    ArticleDTO createArticle(ArticleDTO request, UserDetails userDetails);

    ArticleDTO updateArticle(ArticleDTO request, Long articleId, UserDetails userDetails);

    ArticleDTO submitArticle(Long id, UserDetails userDetails);

    ArticleDTO approveArticle(Long id);

    ArticleDTO rejectArticle(Long id, RejectArticleRequest rejectArticleRequest);

    ArticleDTO publishArticle(Long id);

    List<ArticleDTO> searchArticles(String name, String content);
}
