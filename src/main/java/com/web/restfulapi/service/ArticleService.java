package com.web.restfulapi.service;

import com.web.restfulapi.model.Article;
import com.web.restfulapi.repository.ArticleRepository;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public interface ArticleService {

    public Article createArticle(Article article);

    public Article updateArticle(Article article);

    public Article getArticleById(Long articleId);

    public Article submitArticle(Article article);

}
