package news.news_management.article.service;

import news.news_management.article.model.Article;
import news.news_management.article.model.ArticleStatus;
import news.news_management.article.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    private static final Logger logger = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Override
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public List<Article> getArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId).orElse(null);
    }

    @Override
    public List<Article> searchArticlesByNameAndContent(String name, String content) {
        // Call the data access layer or use your ORM to perform the search
        return articleRepository.findByNameContainingAndContentContaining(name, content);
    }

    @Override
    public List<Article> searchArticlesByName(String name) {
        return articleRepository.findByName(name);
    }

    @Override
    public List<Article> searchArticlesByContent(String content) {
        return articleRepository.findByContent(content);
    }



    @Override
    public List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate) {
        List<Article> articles = new ArrayList<>();

        if (status != null && startDate != null && endDate != null) {
            // Filter by status and date range
            articles = articleRepository.findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else if (status != null) {
            // Filter by status only
            articles = articleRepository.findByStatusOrderByStatusDescCreatedAtDesc(status);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            articles = articleRepository.findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else {
            // No filters, return all articles
            articles = articleRepository.findAllByOrderByStatusDescCreatedAtDesc();
        }

        return articles;
    }


}

