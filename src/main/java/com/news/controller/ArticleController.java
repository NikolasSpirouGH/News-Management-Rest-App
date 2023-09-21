package com.news.controller;

import com.news.entity.Article;
import com.news.entity.ArticleStatus;
import com.news.entity.User;
import com.news.payload.ArticleDTO;
import com.news.payload.RejectArticleRequest;
import com.news.service.ArticleService;
import com.news.service.CommentService;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@Validated
@RequestMapping("/articles")
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;
    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);
    @PostMapping("/saveArticle")
    @PreAuthorize("hasAnyAuthority('JOURNALIST','ADMIN','EDITOR')")
    public ResponseEntity<ArticleDTO> saveArticle(@Valid @RequestBody ArticleDTO request , @AuthenticationPrincipal UserDetails userDetails) {
        ArticleDTO articleResponse = articleService.createArticle(request, userDetails);
        return new ResponseEntity<>(articleResponse, HttpStatus.CREATED);
    }

    @PutMapping("/updateArticle/{articleId}")
    @PreAuthorize("hasAnyAuthority( 'JOURNALIST','EDITOR', 'ADMIN')")
    public ResponseEntity<ArticleDTO> updateArticle(@Valid @RequestBody ArticleDTO articleDTO, @PathVariable(name="articleId") Long articleId, @AuthenticationPrincipal UserDetails userDetails){
        ArticleDTO articleResponse = articleService.updateArticle(articleDTO, articleId,  userDetails);
        return ResponseEntity.ok(articleResponse);
    }

    @PutMapping("/submitArticle/{articleId}")
    @PreAuthorize("hasAnyAuthority( 'JOURNALIST','EDITOR', 'ADMIN')")
    public ResponseEntity<ArticleDTO> submitArticle(@PathVariable Long articleId, @AuthenticationPrincipal UserDetails userDetails) {
        ArticleDTO articleResponse = articleService.submitArticle(articleId, userDetails);
        return ResponseEntity.ok(articleResponse);
    }
    @PreAuthorize("hasAnyAuthority('EDITOR', 'ADMIN')")
    @PutMapping("/approveArticle/{articleId}")
    public ResponseEntity<ArticleDTO> approveArticle(@PathVariable Long articleId) {
        ArticleDTO articleResponse = articleService.approveArticle(articleId);
        return ResponseEntity.ok(articleResponse);
    }

    @PreAuthorize("hasAnyAuthority('EDITOR', 'ADMIN')")
    @PutMapping("/rejectArticle/{articleId}")
    public ResponseEntity<ArticleDTO> rejectArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody RejectArticleRequest rejectArticleRequest) {
        ArticleDTO rejectedArticle = articleService.rejectArticle(articleId, rejectArticleRequest);
        return ResponseEntity.ok(rejectedArticle);
    }

    @PreAuthorize("hasAnyAuthority('JOURNALIST','ADMIN')")
    @PutMapping("/publishArticle/{articleId}")
    public ResponseEntity<ArticleDTO> publishArticle(@PathVariable Long articleId) {
        ArticleDTO publishedArticle = articleService.publishArticle(articleId);
        return ResponseEntity.ok(publishedArticle);
    }

    @GetMapping("/searchArticles")
    public ResponseEntity<List<ArticleDTO>> searchArticlesController(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String content
    ) {
        List<ArticleDTO> results = articleService.searchArticlesWithFilters(userDetails, name, content);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/getArticleById/{id}")
   @PreAuthorize("hasAnyAuthority('JOURNALIST','ADMIN','EDITOR')")
    public ResponseEntity<ArticleDTO> getArticleById(@PathVariable long id){
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @GetMapping("/listArticlesWithFilters")
    public ResponseEntity<List<ArticleDTO>> getArticlesWithFilters(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<ArticleDTO> articles = articleService.getArticlesWithFilters(userDetails, status, startDate, endDate);
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/getArticles")
    public ResponseEntity<List<ArticleDTO>> getArticles(@AuthenticationPrincipal UserDetails userDetails) {
        System.out.println("helo controller");
        List<ArticleDTO> articles = articleService.getArticles(userDetails);
        return ResponseEntity.ok(articles);

    }
}

