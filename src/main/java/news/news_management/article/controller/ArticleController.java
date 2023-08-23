package news.news_management.article.controller;

import news.news_management.article.dto.ArticleResponseWithComments;
import news.news_management.article.model.Article;
import news.news_management.article.model.ArticleStatus;
import news.news_management.comment.model.Comment;
import news.news_management.comment.service.CommentService;
import news.news_management.topic.dto.TopicRequest;
import news.news_management.topic.model.Topic;
import news.news_management.topic.service.TopicService;
import news.news_management.article.dto.ArticleRequest;
import news.news_management.article.service.ArticleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/articles")
public class ArticleController {



    @Autowired
    private ArticleService articleService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private CommentService commentService;

    @Transactional
    @PostMapping("/saveArticle")
    public ResponseEntity<Article> saveArticle(@Valid @RequestBody ArticleRequest request) {
        Article article = new Article();

            Article existingArticle = articleService.getArticleByName(request.getName());
            if (existingArticle != null) {
                throw new RuntimeException("Article with this name already exists");
            }

            article.setName(request.getName());
            article.setContent(request.getContent());
            article.setStatus(ArticleStatus.CREATED);

            List<Topic> topics = new ArrayList<>();
            for (TopicRequest topicRequest : request.getTopics()) {
                Topic existingTopic = topicService.getTopicByName(topicRequest.getName());
                if (existingTopic != null) {
                    topics.add(existingTopic); // Add the existing topic to the list
                } else {
                    throw new RuntimeException("Topic doesnt exist");
                }
            }

            article.setTopics(topics);

            return new ResponseEntity<>(articleService.saveArticle(article), HttpStatus.CREATED);

    }

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

//    @GetMapping("/searchArticles")
//    public ResponseEntity<List<Article>> searchArticles(@RequestParam(required = false) String name, @RequestParam(required = false) String content) {
//        List<Article> results = new ArrayList<>();
//
//        if (name != null && content != null) {
//            // Search by both title and content
//            results = articleService.searchArticlesByNameAndContent(name, content);
//        } else if (name != null) {
//            // Search by title
//            results = articleService.searchArticlesByName(name);
//        } else if (content != null) {
//            // Search by content
//            results = articleService.searchArticlesByContent(content);
//        } else return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//
//        return new ResponseEntity<>(results,HttpStatus.OK);
//    }

    @GetMapping("/getArticleById/{articleId}")
    public ResponseEntity<String> getArticleById(@PathVariable Long articleId) {
        Article article = articleService.getArticleById(articleId);

        if (article == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<Comment> comments = commentService.getCommentsByArticleId(articleId);

        System.out.println(comments);

        ArticleResponseWithComments response = new ArticleResponseWithComments(article, comments);

        return new ResponseEntity<>(response.getArticle() + "\n\n Comments : \n\n" + response.getComments(), HttpStatus.OK);
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

