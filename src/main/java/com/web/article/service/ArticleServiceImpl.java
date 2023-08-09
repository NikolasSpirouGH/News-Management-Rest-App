package com.web.article.service;

import com.web.article.model.Article;
import com.web.article.model.ArticleStatus;
import com.web.article.repository.ArticleRepository;
import com.web.topic.model.Topic;
import com.web.topic.service.TopicService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepo;

    @Autowired
    private TopicService topicService;

    @Autowired
    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepo = articleRepository;
    }

    @Transactional
    @Override
    public Article createArticle(Article article, List<String> topicNames) {
        // Set article status
        article.setStatus(ArticleStatus.CREATED);

        // Create or retrieve topics and associate them with the article
        List<Topic> topics = new ArrayList<>();
        for (String topicName : topicNames) {
            Topic topic = topicService.getOrCreateTopicByName(topicName);
            topics.add(topic);
        }
        article.setTopics(topics);

        return articleRepo.save(article);
    }

    @Transactional
    @Override
    public Article updateArticle(Article article) {
        return articleRepo.save(article);
    }

    public Article getArticleById(Long articleId) {
        Optional<Article> optionalArticle = articleRepo.findById(articleId);
        return optionalArticle.orElse(null);
    }
    @Transactional
    @Override
    public Article submitArticle(Article article) {
        article.setStatus(ArticleStatus.SUBMITTED);
        return articleRepo.save(article);
    }

    @Override
    public List<Article> getAllArticles() {
        return articleRepo.findAll();
    }
}
