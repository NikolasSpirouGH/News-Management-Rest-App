package news.service;

import news.dto.ArticleRequest;
import news.dto.ArticleResponseWithComments;
import news.exception.ArticleAlreadyExistsException;
import news.exception.ArticleNotFoundException;
import news.exception.TopicNotFoundException;
import news.entity.Article;
import news.entity.ArticleStatus;
import news.repository.ArticleRepository;
import news.dto.CommentResponse;
import news.entity.Comment;
import news.dto.TopicRequest;
import news.entity.Topic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TopicService topicService;

    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Override
    public Article saveArticle(Article article) {
        return articleRepository.save(article);
    }

    @Override
    public Article getArticleByName(String name) {
        return articleRepository.findByName(name);
    }

    @Override
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow();
    }

    @Override
    public List<ArticleResponseWithComments> searchArticlesByNameAndContent(String title, String content) {
        List<Article> articles = articleRepository.findByNameContainingAndContentContaining(title, content);
        return convertToArticleResponseWithCommentsList(articles);
    }

    @Override
    public ArticleResponseWithComments searchArticlesByName(String title) {
        Article article = articleRepository.findByName(title);
        if (article != null) {
            return convertToArticleResponseWithComments(article);
        }
        return null;
    }

    @Override
    public List<ArticleResponseWithComments> searchArticlesByContent(String content) {
        List<Article> articles = articleRepository.findByContentContaining(content);
        return convertToArticleResponseWithCommentsList(articles);
    }

    private List<ArticleResponseWithComments> convertToArticleResponseWithCommentsList(List<Article> articles) {
        // Implement the logic to convert a list of Article entities to a list of ArticleResponseWithComments DTOs
        List<ArticleResponseWithComments> responseList = new ArrayList<>();
        for (Article article : articles) {
            responseList.add(convertToArticleResponseWithComments(article));
        }
        return responseList;
    }

    private ArticleResponseWithComments convertToArticleResponseWithComments(Article article) {
        ArticleResponseWithComments response = new ArticleResponseWithComments();
        response.setArticleId(article.getArticleId());
        response.setArticleName(article.getName());
        response.setArticleContent(article.getContent());
        // Set other properties as needed

        // Assuming you have comments associated with the article
        List<Comment> comments = article.getComments();
        List<CommentResponse> commentResponses = new ArrayList<>();
        for (Comment comment : comments) {
            CommentResponse commentResponse = new CommentResponse();
            commentResponse.setCommentId(comment.getCommentId());
            commentResponse.setCommentText(comment.getText());
            // Set other comment properties
            commentResponses.add(commentResponse);
        }
        response.setComments(commentResponses);

        return response;
    }

    @Override
    public List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate) {
        List<Article> articles = new ArrayList<>();

        if (status != null && startDate != null && endDate != null) {
            // Filter by status and date range
            articles = articleRepository.findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else if (status != null) {
            // Filter by status only
            articles = articleRepository.findByStatusOrderByStatusDescCreatedAtDesc(status);
        } else if (startDate != null && endDate != null) {
            // Filter by date range only
            articles = articleRepository.findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else {
            // No filters, return all articles
            articles = articleRepository.findAllByOrderByStatusDescCreatedAtDesc();
        }

        return articles;
    }

    @Override
    @Transactional
    public Article createArticle(ArticleRequest request) {
        // Check if an article with the same name already exists
        Article existingArticle = articleRepository.findByName(request.getName());
        if (existingArticle != null) {
            logger.error("Article with name '{}' already exists", request.getName());
            throw new ArticleAlreadyExistsException("Article with name '" + request.getName() + "' already exists");
        }
        logger.info("Creating article: {}", request.getName());

        Article article = new Article();
        article.setName(request.getName());
        article.setContent(request.getContent());
        article.setStatus(ArticleStatus.CREATED);

        List<Topic> topics = new ArrayList<>();
        for (TopicRequest topicRequest : request.getTopics()) {
            Topic existingTopic = topicService.getTopicByName(topicRequest.getName());
            if (existingTopic != null) {
                topics.add(existingTopic);
            } else {
                logger.error("Topic with name '{}' doesn't exist", topicRequest.getName());
                throw new TopicNotFoundException("Topic with name '" + topicRequest.getName() + "' doesn't exist");
            }
        }
        article.setTopics(topics);
        articleRepository.save(article);

        logger.info("Article created successfully: {}", article.getName());

        return article;
    }

    @Override
    @Transactional
    @Modifying
    public Article updateArticle(Long articleId, ArticleRequest request) {
        try {
            Optional<Article> optionalArticle = articleRepository.findById(articleId);
            Article existingArticle = optionalArticle.orElseThrow(() -> new ArticleNotFoundException("Article not found"));


            if (existingArticle == null) {
                logger.warn("Article with ID {} not found.", articleId);
                throw new ArticleNotFoundException("Article not found");
            }

            if (existingArticle.getStatus() == ArticleStatus.PUBLISHED) {
                logger.warn("Article with ID {} is already published.", articleId);
                throw new ArticleAlreadyExistsException("Article is already published");
            }

            List<Topic> updatedTopics = new ArrayList<>();
            for (TopicRequest topicRequest : request.getTopics()) {
                Topic existingTopic = topicService.getTopicByName(topicRequest.getName());

                if (existingTopic != null) {
                    updatedTopics.add(existingTopic);
                } else {
                    logger.warn("Invalid topic: {}", topicRequest.getName());
                    throw new TopicNotFoundException("Invalid topic: " + topicRequest.getName());
                }
            }
            // Update article properties
            existingArticle.setName(request.getName());
            existingArticle.setContent(request.getContent());
            existingArticle.setTopics(updatedTopics);

            // Save the updated article
            Article updatedArticle = articleRepository.save(existingArticle);

            logger.info("Article with ID {} updated successfully.", articleId);
            return updatedArticle;
        } catch (Exception e) {
            logger.error("An error occurred while updating the article with ID " + articleId, e);
            throw e;
        }
    }

}


