package news.service;

import news.dto.ArticleRequest;
import news.dto.ArticleResponseWithComments;
import news.entity.Article;
import news.entity.ArticleStatus;


import java.time.LocalDate;
import java.util.List;

public interface ArticleService {

    public Article saveArticle(Article article);

    Article getArticleByName(String name);

    Article getArticleById(Long articleId);

    List<ArticleResponseWithComments> searchArticlesByNameAndContent(String name, String content);

    ArticleResponseWithComments searchArticlesByName(String name);

    List<ArticleResponseWithComments> searchArticlesByContent(String content);

    List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate);

    Article createArticle(ArticleRequest request);

    Article updateArticle(Long articleId,ArticleRequest request);
}
