package com.news.service.impl;

import com.news.entity.Article;
import com.news.entity.Topic;
import com.news.entity.User;
import com.news.exception.InvalidUserNameException;
import com.news.payload.ArticleDTO;
import com.news.payload.TopicDTO;
import com.news.repository.ArticleRepository;
import com.news.repository.TopicRepository;
import com.news.repository.UserRepository;
import com.news.security.JwtTokenUtil;
import com.news.service.ArticleService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import com.news.exception.NewsAPIException;
import com.news.exception.ResourceNotFoundException;
import com.news.entity.ArticleStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final ArticleRepository articleRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final JwtTokenUtil jwtTokenUtil;
    private static final Logger logger = LoggerFactory.getLogger(ArticleService.class);

    @Override
    @Transactional
    public ArticleDTO createArticle(ArticleDTO articleDTO, @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername());

        if(user == null) {
            throw new ResourceNotFoundException("User","name",articleDTO.getArticleId());
        }
        System.out.println(user.getUsername());

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
        article.setUser(user);
        article.setStatus(ArticleStatus.CREATED);
        System.out.println(article.getTopics().get(0).getName());
        System.out.println("DTO " + " " + articleDTO.getTopics().get(0).getUsername());
        System.out.println("date article "  + article.getCreatedAt());
        System.out.println("DTO date article "  + articleDTO.getCreatedAt());

        articleRepository.save(article);
        ArticleDTO resultArticle = modelMapper.map(article,ArticleDTO.class);

        List<TopicDTO> topicsList = new ArrayList<>();
        for(Topic topic : article.getTopics()) {
            String username = topic.getUser().getUsername();
            TopicDTO topicDTO = modelMapper.map(topic, TopicDTO.class);
            topicDTO.setUsername(username);
            topicsList.add(topicDTO);
        }
        resultArticle.setTopics(topicsList);
        resultArticle.setCreatedAt(LocalDate.now());

        return resultArticle;
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
        ArticleDTO articleDTO = getArticleById(articleId);

        Article article = mapToEntity(articleDTO);
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
        ArticleDTO articleDTO = getArticleById(id);
        Article article = mapToEntity(articleDTO);
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
        ArticleDTO articleDTO = getArticleById(articleId);
        Article article = mapToEntity(articleDTO);
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

    public Article getArticleByName(String name) {
        return articleRepository.findByName(name);
    }

    @Override
    public ArticleDTO getArticleById(Long articleId) {
        Article article =  articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", articleId));
        return mapToDTO(article);
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


}


