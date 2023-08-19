package com.web.article.service;

import com.web.article.model.Article;
import org.springframework.stereotype.Service;


import java.util.List;

public interface ArticleService {


    public Article saveArticle(Article article);

    List<Article> getArticles();

    Article getArticleById(Long articleId);
}
