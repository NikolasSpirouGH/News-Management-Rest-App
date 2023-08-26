package news.controller;

import news.dto.ArticleResponseWithComments;
import news.entity.Article;
import news.entity.ArticleStatus;
import news.dto.CommentResponse;
import news.entity.Comment;
import news.service.CommentService;
import news.dto.TopicRequest;
import news.entity.Topic;
import news.service.TopicService;
import news.dto.ArticleRequest;
import news.service.ArticleService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@RequestMapping("/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private CommentService commentService;

    private static final Logger logger = LoggerFactory.getLogger(ArticleController.class);

    @Transactional
    @PostMapping("/saveArticle")
    public ResponseEntity<Article> saveArticle(@Valid @RequestBody ArticleRequest request) {
        Article article = articleService.createArticle(request);
        return new ResponseEntity<>(article, HttpStatus.CREATED);
    }

    @Modifying
    @Transactional
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

    @Transactional
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

    @Transactional
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

    @Transactional
    @PutMapping("/rejectArticle/{articleId}")
    public ResponseEntity<Article> rejectArticle(@PathVariable Long articleId, @RequestBody String rejectionReason) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        article.setStatus(ArticleStatus.REJECTED);
        article.setRejectionReason(rejectionReason);

        Article rejectedArticle = articleService.saveArticle(article);

        return new ResponseEntity<>(rejectedArticle, HttpStatus.OK);
    }

    @Transactional
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
        article.setPublishedAt(LocalDate.now());

        Article publishedArticle = articleService.saveArticle(article);

        return new ResponseEntity<>(publishedArticle, HttpStatus.OK);
    }

    @GetMapping("/searchArticles")
    public ResponseEntity<List<ArticleResponseWithComments>> searchArticles(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content
    ) {
        logger.info("Received searchArticles request with title: {} and content: {}", title, content);

        List<ArticleResponseWithComments> results = new ArrayList<>();

        if (title != null && content != null) {
            logger.debug("Searching articles by both title and content");
            results = articleService.searchArticlesByNameAndContent(title, content);
        } else if (title != null) {
            logger.debug("Searching articles by title");
            results.add(articleService.searchArticlesByName(title));
        } else if (content != null) {
            logger.debug("Searching articles by content");
            results = articleService.searchArticlesByContent(content);
        } else {
            logger.warn("Invalid request parameters");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("Returning {} search results", results.size());
        return new ResponseEntity<>(results, HttpStatus.OK);
    }

    @GetMapping("/getArticleById/{articleId}")
    public ResponseEntity<ArticleResponseWithComments> getArticleById(@PathVariable Long articleId) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentService.getCommentsByArticleId(articleId);

        // Convert comments to CommentResponse objects
        List<CommentResponse> responseComments = comments.stream()
                .map(comment -> new CommentResponse(
                        comment.getCommentId(),
                        comment.getText(),
                        comment.getCreatedAt(),
                        comment.getAuthorName(),
                        comment.getStatus()
                                ))
                .collect(Collectors.toList());

        ArticleResponseWithComments response = new ArticleResponseWithComments(
                article.getName(),
                article.getArticleId(),
                article.getContent(),
                responseComments
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/listArticles")
    public ResponseEntity<List<Article>> listAllArticles(
            @RequestParam(required = false) ArticleStatus status,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        List<Article> articles = articleService.listAllArticlesWithFilters(status, startDate, endDate);

        return new ResponseEntity<>(articles, HttpStatus.OK);
    }

    }

