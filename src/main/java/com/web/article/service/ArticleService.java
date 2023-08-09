package com.web.article.service;

import com.web.article.model.Article;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
public interface ArticleService {

    public Article createArticle(Article article, List<String> topicNames);

    public Article updateArticle(Article article);

    public Article getArticleById(Long articleId);

    public Article submitArticle(Article article);

    public List<Article> getAllArticles();

}
