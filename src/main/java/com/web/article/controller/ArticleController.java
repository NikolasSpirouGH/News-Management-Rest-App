package com.web.article.controller;


import com.web.article.dto.ArticleRequest;
import com.web.article.model.Article;
import com.web.article.model.ArticleStatus;
import com.web.article.service.ArticleService;
import com.web.topic.dto.TopicRequest;
import com.web.topic.model.Topic;
import com.web.topic.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TopicService topicService;

    @PostMapping("/saveArticle")
    public ResponseEntity<Article> saveArticle(@Valid @RequestBody ArticleRequest request) {
        Article article = new Article();
        article.setName(request.getName());
        article.setContent(request.getContent());
        article.setStatus(ArticleStatus.CREATED);

        List<Topic> topics = new ArrayList<>();
        for (TopicRequest topicRequest : request.getTopics()) {
            Topic existingTopic = topicService.getTopicByName(topicRequest.getName());
            if (existingTopic != null) {
                topics.add(existingTopic); // Add the existing topic to the list
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        article.setTopics(topics);

        return new ResponseEntity<>(articleService.saveArticle(article), HttpStatus.CREATED);
    }
    @PutMapping("/updateArticle/{articleId}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long articleId, @Valid @RequestBody ArticleRequest request) {
        Article existingArticle = articleService.getArticleById(articleId);
        if (existingArticle == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (existingArticle.getStatus() == ArticleStatus.PUBLISHED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        existingArticle.getTopics().clear();

        existingArticle.setName(request.getName());
        existingArticle.setContent(request.getContent());

        List<Topic> updatedTopics = new ArrayList<>();
        for (TopicRequest topicRequest : request.getTopics()) {
            Topic existingTopic = topicService.getTopicByName(topicRequest.getName());

            if (existingTopic != null) {

                updatedTopics.add(existingTopic);
            } else {
                System.out.println("Bad Topics");
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }
        existingArticle.setTopics(updatedTopics);

        Article updatedArticle = articleService.saveArticle(existingArticle);
        return new ResponseEntity<>(updatedArticle, HttpStatus.OK);
    }

    @PutMapping("/submitArticle/{articleId}")
    public ResponseEntity<Article> submitArticle(@PathVariable Long articleId) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (article.getStatus() != ArticleStatus.CREATED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        article.setStatus(ArticleStatus.SUBMITTED);

        Article submittedArticle = articleService.saveArticle(article);

        return new ResponseEntity<>(submittedArticle, HttpStatus.OK);
    }

    @PutMapping("/acceptArticle/{articleId}")
    public ResponseEntity<Article> acceptArticle(@PathVariable Long articleId) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        article.setStatus(ArticleStatus.APPROVED);

        Article acceptedArticle = articleService.saveArticle(article);

        return new ResponseEntity<>(acceptedArticle, HttpStatus.OK);
    }

    @PutMapping("/rejectArticle/{articleId}")
    public ResponseEntity<Article> rejectArticle(@PathVariable Long articleId, @RequestBody String rejectionReason) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        ArticleStatus originalStatus = article.getStatus();

        article.setStatus(ArticleStatus.REJECTED);
        article.setRejectionReason(rejectionReason);

        Article rejectedArticle = articleService.saveArticle(article);

            rejectedArticle.setStatus(ArticleStatus.CREATED);
            rejectedArticle = articleService.saveArticle(rejectedArticle);

        return new ResponseEntity<>(rejectedArticle, HttpStatus.OK);
    }

    @PutMapping("/publishArticle/{articleId}")
    public ResponseEntity<Article> publishArticle(@PathVariable Long articleId) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (article.getStatus() != ArticleStatus.APPROVED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        article.setPublishedAt(LocalDateTime.now());

        Article publishedArticle = articleService.saveArticle(article);

        return new ResponseEntity<>(publishedArticle, HttpStatus.OK);
    }



    @GetMapping("/getArticles")
    public ResponseEntity<List<Article>> getArticles() {
        return new ResponseEntity<>(articleService.getArticles(),HttpStatus.OK);
    }
    }

