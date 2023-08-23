package news.news_management.article.service;

import news.news_management.article.model.Article;
import news.news_management.article.model.ArticleStatus;


import java.time.LocalDate;
import java.util.List;

public interface ArticleService {

    public Article saveArticle(Article article);

    Article getArticleByName(String name);

    Article getArticleById(Long articleId);

    List<Article> searchArticlesByNameAndContent(String name, String content);

//    List<Article> searchArticlesByName(String name);

    List<Article> searchArticlesByContent(String content);

    List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate);

}
