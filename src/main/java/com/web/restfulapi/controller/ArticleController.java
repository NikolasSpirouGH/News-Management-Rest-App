package com.web.restfulapi.controller;

import com.web.restfulapi.model.Article;
import com.web.restfulapi.model.ArticleStatus;
import com.web.restfulapi.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/articles")
public class ArticleController {

    private final ArticleService articleService;

    @Autowired
    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody Article article) {
        return new ResponseEntity<Article>(articleService.createArticle((article)), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Article> updateArticle(@RequestParam Long id, @RequestBody Article article) {
        article.setId(id);
        return new ResponseEntity<Article>(articleService.updateArticle(article), HttpStatus.OK);
    }

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
    }

