package news.service;

import news.payload.ArticleDTO;
import news.entity.Article;
import news.payload.ArticleResponse;

import java.util.List;

public interface ArticleService {
//
//    public Article saveArticle(Article article);

    Article getArticleByName(String name);

    Article getArticleById(Long articleId);
//
//    List<ArticleResponseWithComments> searchArticlesByNameAndContent(String name, String content);
//
//    ArticleResponseWithComments searchArticlesByName(String name);
//
//    List<ArticleResponseWithComments> searchArticlesByContent(String content);
//
//    List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate);

    ArticleDTO createArticle(ArticleDTO request);

    ArticleDTO updateArticle(ArticleDTO request, Long articleId);

    ArticleDTO submitArticle(Long id);

    ArticleDTO approveArticle(Long id);

    ArticleDTO rejectArticle(Long id, String rejectionReason);

    ArticleDTO publishArticle(Long id);

    List<ArticleDTO> searchArticles(String name, String content);
}
