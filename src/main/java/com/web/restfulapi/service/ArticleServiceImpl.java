package com.web.restfulapi.service;

import com.web.restfulapi.model.Article;
import com.web.restfulapi.model.ArticleStatus;
import com.web.restfulapi.repository.ArticleRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ArticleServiceImpl implements ArticleService {


    private ArticleRepository articleRepository;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Transactional
    @Override
    public Article createArticle(Article article) {
        article.setStatus(ArticleStatus.CREATED);
        return articleRepository.save(article);
    }

    @Override
    public Article updateArticle(Article article) {
        return articleRepository.save(article);
    }

    public Article getArticleById(Long articleId) {
        Optional<Article> optionalArticle = articleRepository.findById(articleId);
        return optionalArticle.orElse(null);
    }
    @Transactional
    @Override
    public Article submitArticle(Article article) {
        article.setStatus(ArticleStatus.SUBMITTED);
        return articleRepository.save(article);
    }
}
