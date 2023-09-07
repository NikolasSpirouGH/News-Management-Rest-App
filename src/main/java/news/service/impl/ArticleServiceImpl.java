package news.service.impl;

import lombok.RequiredArgsConstructor;
import news.exception.NewsAPIException;
import news.exception.ResourceNotFoundException;
import news.payload.ArticleDTO;
import news.entity.Article;
import news.entity.ArticleStatus;
import news.payload.ArticleResponse;
import news.repository.ArticleRepository;
import news.payload.TopicDTO;
import news.entity.Topic;
import news.repository.TopicRepository;
import news.service.ArticleService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;

    private final TopicRepository topicRepository;

    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleDTO articleDTO) {

        List<Topic> topics = new ArrayList<>();
        for (TopicDTO topicDTO : articleDTO.getTopics()) {
            Topic topic = topicRepository.findByName(topicDTO.getName());
            if (topic != null) {
                topics.add(topic);
            } else {
                logger.error("Topic with name '{}' doesn't exist", topicDTO.getName());
                throw new ResourceNotFoundException("Topic", "id", topicDTO.getTopicId());
            }
        }
        Article article = mapToEntity(articleDTO);
        article.setTopics(topics);
        article.setStatus(ArticleStatus.CREATED);
        Article newArticle = articleRepository.save(article);

        return mapToDTO(newArticle);
    }

    @Override
    @Transactional
    public ArticleDTO updateArticle(ArticleDTO articleDTO, Long id) {
        // get post by id from the database
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        if (article == null) {
                logger.warn("Article with ID {} not found.", id);
                throw new ResourceNotFoundException("Article","id",articleDTO.getArticleId());
            }

            if (article.getStatus() == ArticleStatus.PUBLISHED) {
                logger.warn("Article with ID {} is already published.", id);
                throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Article is already published");
            }
        List<Topic> topics = new ArrayList<>();
        for (TopicDTO topicDTO : articleDTO.getTopics()) {
            Topic topic = topicRepository.findByName(topicDTO.getName());
            if (topic != null) {
                topics.add(topic);
            } else {
                logger.error("Topic with name '{}' doesn't exist", topicDTO.getName());
                throw new ResourceNotFoundException("Topic", "id", topicDTO.getTopicId());
            }
        }
        article.setName(articleDTO.getName());
        article.setContent(articleDTO.getContent());
        article.setTopics(topics);
        Article updatedPost = articleRepository.save(article);
        return mapToDTO(updatedPost);
    }

    @Override
    @Transactional
    public ArticleDTO submitArticle(Long articleId) {
        Article article = getArticleById(articleId);

        if (article == null) {
            throw new ResourceNotFoundException("Article","id",article.getArticleId());
        }

        if (article.getStatus() != ArticleStatus.CREATED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in create state");
        }
        article.setStatus(ArticleStatus.SUBMITTED);
        logger.info("Article with ID {} submitted successfully.", articleId);

        articleRepository.save(article);
        return mapToDTO(article);
    }

    @Override
    @Transactional
    public ArticleDTO approveArticle(Long id) {
        Article article = getArticleById(id);

        if (article == null) {
            throw new ResourceNotFoundException("Article","id",article.getArticleId());
        }

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in submit state");
        }
        article.setStatus(ArticleStatus.APPROVED);
        logger.info("Article with ID {} submitted successfully.", id);

        articleRepository.save(article);
        return mapToDTO(article);
    }

    @Override
    @Transactional
    public ArticleDTO rejectArticle(Long id, String rejectionReason) {
        // Get the article by ID from the database
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in create state");
        }

        // Set the rejection reason
        article.setRejectionReason(rejectionReason);

        // Set the status to REJECTED
        article.setStatus(ArticleStatus.REJECTED);

        // Save the rejected article
        articleRepository.save(article);

        logger.info("Article with ID {} has been rejected with reason: {}", id, rejectionReason);

        return mapToDTO(article);
    }

    @Override
    @Transactional
    public ArticleDTO publishArticle(Long articleId) {
        Article article = getArticleById(articleId);

        if (article == null) {
            throw new ResourceNotFoundException("Article", "id", articleId);
        }

        if (article.getStatus() != ArticleStatus.APPROVED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in approved state");
        }

        article.setStatus(ArticleStatus.PUBLISHED);
        logger.info("Article with ID {} published successfully.", articleId);

        articleRepository.save(article);
        return mapToDTO(article);
    }

    @Override
    public List<ArticleDTO> searchArticles(String name, String content) {
        try {
            List<Article> results = new ArrayList<>();

            if (name != null && content != null) {
                logger.debug("Searching articles by both title and content");
                results = articleRepository.findByNameContainingAndContentContaining(name, content);
            } else if (name != null) {
                logger.debug("Searching articles by title");
                results.add(articleRepository.findByNameContaining(name));
            } else if (content != null) {
                logger.debug("Searching articles by content");
                results = articleRepository.findByContentContaining(content);
            } else {
                logger.warn("Full searching");
                results = articleRepository.findAll();
            }

            List<ArticleDTO> resultsDTO = results.stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());

            return resultsDTO;
        } catch (Exception ex) {
            // Εδώ μπορείτε να προσθέσετε την προσαρμοσμένη επεξεργασία του Exception
            // Π.χ., μπορείτε να καταγράψετε το λάθος στο log και να επιστρέψετε μια κατάλληλη απάντηση
            logger.error("An error occurred while searching articles.", ex);
            throw new NewsAPIException(HttpStatus.NOT_ACCEPTABLE, "Invalid Arguments");
        }
    }

    private ArticleDTO mapToDTO(Article article){
        ArticleDTO postDto = modelMapper.map(article, ArticleDTO.class);
//        PostDto postDto = new PostDto();
//        postDto.setId(post.getId());
//        postDto.setTitle(post.getTitle());
//        postDto.setDescription(post.getDescription());
//        postDto.setContent(post.getContent());
        return postDto;
    }

    // convert DTO to entity
    private Article mapToEntity(ArticleDTO articleDTO){
        Article post = modelMapper.map(articleDTO, Article.class);
//        Post post = new Post();
//        post.setTitle(postDto.getTitle());
//        post.setDescription(postDto.getDescription());
//        post.setContent(postDto.getContent());
        return post;
    }

    @Override
    public Article getArticleByName(String name) {
        return articleRepository.findByName(name);
    }

    @Override
    public Article getArticleById(Long articleId) {
        return articleRepository.findById(articleId).orElseThrow();
    }

//
//    @Override
//    public List<ArticleResponseWithComments> searchArticlesByNameAndContent(String title, String content) {
//        List<Article> articles = articleRepository.findByNameContainingAndContentContaining(title, content);
//        return convertToArticleResponseWithCommentsList(articles);
//    }
//
//    @Override
//    public ArticleResponseWithComments searchArticlesByName(String title) {
//        Article article = articleRepository.findByName(title);
//        if (article != null) {
//            return convertToArticleResponseWithComments(article);
//        }
//        return null;
//    }
//
//    @Override
//    public List<ArticleResponseWithComments> searchArticlesByContent(String content) {
//        List<Article> articles = articleRepository.findByContentContaining(content);
//        return convertToArticleResponseWithCommentsList(articles);
//    }
//
//    private List<ArticleResponseWithComments> convertToArticleResponseWithCommentsList(List<Article> articles) {
//        // Implement the logic to convert a list of Article entities to a list of ArticleResponseWithComments DTOs
//        List<ArticleResponseWithComments> responseList = new ArrayList<>();
//        for (Article article : articles) {
//            responseList.add(convertToArticleResponseWithComments(article));
//        }
//        return responseList;
//    }
//
//    private ArticleResponseWithComments convertToArticleResponseWithComments(Article article) {
//        ArticleResponseWithComments response = new ArticleResponseWithComments();
//        response.setArticleId(article.getArticleId());
//        response.setArticleName(article.getName());
//        response.setArticleContent(article.getContent());
//        // Set other properties as needed
//
//        // Assuming you have comments associated with the article
//        List<Comment> comments = article.getComments();
//        List<CommentResponse> commentResponses = new ArrayList<>();
//        for (Comment comment : comments) {
//            CommentResponse commentResponse = new CommentResponse();
//            commentResponse.setCommentId(comment.getCommentId());
//            commentResponse.setCommentText(comment.getText());
//            // Set other comment properties
//            commentResponses.add(commentResponse);
//        }
//        response.setComments(commentResponses);
//
//        return response;
//    }
//
//    @Override
//    public List<Article> listAllArticlesWithFilters(ArticleStatus status, LocalDate startDate, LocalDate endDate) {
//        List<Article> articles = new ArrayList<>();
//
//        if (status != null && startDate != null && endDate != null) {
//            // Filter by status and date range
//            articles = articleRepository.findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
//                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
//                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
//        } else if (status != null) {
//            // Filter by status only
//            articles = articleRepository.findByStatusOrderByStatusDescCreatedAtDesc(status);
//        } else if (startDate != null && endDate != null) {
//            // Filter by date range only
//            articles = articleRepository.findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
//                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
//        } else {
//            // No filters, return all articles
//            articles = articleRepository.findAllByOrderByStatusDescCreatedAtDesc();
//        }
//
//        return articles;
//    }



//    @Override
//    @Transactional
//    @Modifying
//    public Article updateArticle(Long articleId, CreateArticleRequest request) {
//        try {
//            Optional<Article> optionalArticle = articleRepository.findById(articleId);
//            Article existingArticle = optionalArticle.orElseThrow(() -> new ArticleNotFoundException("Article not found"));
//
//
//            if (existingArticle == null) {
//                logger.warn("Article with ID {} not found.", articleId);
//                throw new ArticleNotFoundException("Article not found");
//            }
//
//            if (existingArticle.getStatus() == ArticleStatus.PUBLISHED) {
//                logger.warn("Article with ID {} is already published.", articleId);
//                throw new ArticleAlreadyExistsException("Article is already published");
//            }
//
//            List<Topic> updatedTopics = new ArrayList<>();
//            for (TopicRequest topicRequest : request.getTopics()) {
//                Topic existingTopic = topicService.getTopicByName(topicRequest.getName());
//
//                if (existingTopic != null) {
//                    updatedTopics.add(existingTopic);
//                } else {
//                    logger.warn("Invalid topic: {}", topicRequest.getName());
//                    throw new TopicNotFoundException("Invalid topic: " + topicRequest.getName());
//                }
//            }
//            // Update article properties
//            existingArticle.setName(request.getName());
//            existingArticle.setContent(request.getContent());
//            existingArticle.setTopics(updatedTopics);
//
//            // Save the updated article
//            Article updatedArticle = articleRepository.save(existingArticle);
//
//            logger.info("Article with ID {} updated successfully.", articleId);
//            return updatedArticle;
//        } catch (Exception e) {
//            logger.error("An error occurred while updating the article with ID " + articleId, e);
//            throw e;
//        }
//    }

}


