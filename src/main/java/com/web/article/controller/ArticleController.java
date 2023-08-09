package com.web.article.controller;

import com.web.article.dto.ArticleRequest;
import com.web.article.model.Article;
import com.web.article.model.ArticleStatus;
import com.web.article.repository.ArticleRepository;
import com.web.article.service.ArticleService;
import com.web.topic.dto.TopicRequest;
import com.web.topic.model.Topic;
import com.web.topic.repository.TopicRepository;
import com.web.topic.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private ArticleRepository articleRepo;

    @PostMapping("/create")
    public ResponseEntity<Article> createArticle(@Valid @RequestBody ArticleRequest articleRequest) {
        Article article = new Article();
        article.setName(articleRequest.getName());
        article.setContent(articleRequest.getContent());
        article.setStatus(ArticleStatus.CREATED);

        List<Topic> topics = new ArrayList<>();
        for (String topicName : articleRequest.getTopics()) {
            Topic topic = new Topic();
            topic.setName(topicName);
            topic.setArticle(article);
            topics.add(topic);
        }

        article.setTopics(topics);

        articleRepo.save(article);

        return new ResponseEntity<>(article, HttpStatus.CREATED);
    }

    //Submit Article
    @PostMapping("/{id}/submit")
    public ResponseEntity<String> submitArticle(@PathVariable Long id) {
        Article article = articleService.getArticleById(id);

        if (article == null) {
            return ResponseEntity.notFound().build();
        }

        if (article.getStatus() == ArticleStatus.CREATED) {
            articleService.submitArticle(article);
            return ResponseEntity.ok("Article submitted successfully.");
        } else {
            return ResponseEntity.badRequest().body("Article is not in a submittable state.");
        }
    }

    //Get All Articles
    @GetMapping("/readAll")
    public ResponseEntity<List<Article>> readAllArticles() {
        List<Article> articles = articleService.getAllArticles();
        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    }

