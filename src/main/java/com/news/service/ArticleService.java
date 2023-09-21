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

    ArticleDTO createArticle(ArticleDTO request, UserDetails userDetails);

    ArticleDTO updateArticle(ArticleDTO request, Long articleId, UserDetails userDetails);

    ArticleDTO submitArticle(Long id, UserDetails userDetails);

    ArticleDTO approveArticle(Long id);

    ArticleDTO rejectArticle(Long id, RejectArticleRequest rejectArticleRequest);

    ArticleDTO publishArticle(Long id);

    List<ArticleDTO> getArticles(@AuthenticationPrincipal UserDetails userDetails);

    List<ArticleDTO> getArticlesWithFilters(UserDetails userDetails, ArticleStatus status, LocalDate startDate, LocalDate endDate);

    List<ArticleDTO> searchArticlesWithFilters(UserDetails userDetails, String name, String content);

}
