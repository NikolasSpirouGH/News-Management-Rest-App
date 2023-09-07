package com.news.controller;

import com.news.payload.ArticleDTO;
import com.news.service.ArticleService;
import com.news.service.CommentService;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @PostMapping("/saveArticle")
    public ResponseEntity<ArticleDTO> saveArticle(@Valid @RequestBody ArticleDTO request) {
        ArticleDTO articleResponse = articleService.createArticle(request);
        return new ResponseEntity<>(articleResponse, HttpStatus.CREATED);
    }

    @PutMapping("/updateArticle/{articleId}")
    public ResponseEntity<ArticleDTO> updatePost(@Valid @RequestBody ArticleDTO articleDTO, @PathVariable(name="articleId") Long articleId){
        ArticleDTO articleResponse = articleService.updateArticle(articleDTO, articleId);
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/submitArticle/{articleId}")
    public ResponseEntity<ArticleDTO> submitArticle(@PathVariable Long articleId) {
        ArticleDTO articleResponse = articleService.submitArticle(articleId);
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/approveArticle/{articleId}")
    public ResponseEntity<ArticleDTO> approveArticle(@PathVariable Long articleId) {
        ArticleDTO articleResponse = articleService.approveArticle(articleId);
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/rejectArticle/{articleId}/rejectReason")
    public ResponseEntity<ArticleDTO> rejectArticle(
            @PathVariable Long articleId,
            @RequestParam String rejectionReason) {

        ArticleDTO rejectedArticle = articleService.rejectArticle(articleId, rejectionReason);
        return ResponseEntity.ok(rejectedArticle);
    }

    @PutMapping("/publishArticle/{articleId}")
    public ResponseEntity<ArticleDTO> publishArticle(@PathVariable Long articleId) {
        ArticleDTO publishedArticle = articleService.publishArticle(articleId);
        return ResponseEntity.ok(publishedArticle);
    }

    @GetMapping("/searchArticles")
    public ResponseEntity<List<ArticleDTO>> searchArticles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String content
    ) {
        List<ArticleDTO> results = articleService.searchArticles(name, content);
        return ResponseEntity.ok(results);
    }
//
//    @GetMapping("/getArticleById/{articleId}")
//    public ResponseEntity<ArticleResponse> getArticleById(@PathVariable Long articleId) {
//        Article article = articleService.getArticleById(articleId);
//
//        if (article == null) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        List<Comment> comments = commentService.getCommentsByArticleId(articleId);
//
//        // Convert comments to CommentResponse objects
//        List<CommentResponse> responseComments = comments.stream()
//                .map(comment -> new CommentResponse(
//                        comment.getCommentId(),
//                        comment.getText(),
//                        comment.getCreatedAt(),
//                        comment.getAuthorName(),
//                        comment.getStatus()
//                                ))
//                .collect(Collectors.toList());
//
//        ArticleResponse response = new ArticleResponse(
//                article.getName(),
//                article.getArticleId(),
//                article.getContent(),
//                responseComments
//        );
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }
//
//    @GetMapping("/listArticles")
//    public ResponseEntity<List<Article>> listAllArticles(
//            @RequestParam(required = false) ArticleStatus status,
//            @RequestParam(required = false) LocalDate startDate,
//            @RequestParam(required = false) LocalDate endDate
//    ) {
//        List<Article> articles = articleService.listAllArticlesWithFilters(status, startDate, endDate);
//
//        return new ResponseEntity<>(articles, HttpStatus.OK);
//    }

    }

