package com.news.service.impl;

import com.news.entity.Article;
import com.news.entity.Topic;
import com.news.entity.User;
import com.news.payload.ArticleDTO;
import com.news.payload.ArticleIsAlreadySubmittedException;
import com.news.payload.RejectArticleRequest;
import com.news.payload.TopicDTO;
import com.news.repository.ArticleRepository;
import com.news.repository.TopicRepository;
import com.news.repository.UserRepository;
import com.news.service.ArticleService;
import lombok.RequiredArgsConstructor;
import com.news.exception.NewsAPIException;
import com.news.exception.ResourceNotFoundException;
import com.news.entity.ArticleStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
        resultArticle.setUsername(userDetails.getUsername());
        return resultArticle;
    }

    @Override
    @Transactional
    public ArticleDTO updateArticle(ArticleDTO articleDTO, Long id, @AuthenticationPrincipal UserDetails userDetails) {
        boolean isJournalist = false;

        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("JOURNALIST"))) {
            String articleUsername = userDetails.getUsername();
            User articleUser = userRepository.findByUsername(articleUsername);
            if (articleUser == null) {
                throw new AccessDeniedException("You are not authorized to update an article for another user.");
            }
            if (!userDetails.getUsername().equals(articleUser.getUsername())) {
                throw new AccessDeniedException("You are not authorized to create an article for another user.");
            }
            isJournalist = true;
        }
        Article article = articleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));
        //if the article is already published
        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            logger.warn("Article with ID {} is already published.", id);
            throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Article is already published");
        }
        //if user is JOURNALIST and the article is not submitted
        if(isJournalist && article.getStatus() == ArticleStatus.SUBMITTED){
            throw new ArticleIsAlreadySubmittedException(HttpStatus.BAD_REQUEST, "Article with with ID: " + article.getArticleId() + " is already submitted!");
        }
        List<Topic> topics = new ArrayList<>();
        for (TopicDTO topicDTO : articleDTO.getTopics()) {
            Topic topic = topicRepository.findByName(topicDTO.getName());
            if (topic != null && !topics.contains(topic)) {
                topics.add(topic);
            } else {
                logger.error("Topic with name '{}' doesn't exist", topicDTO.getName());
                throw new ResourceNotFoundException("Topic", "id", topicDTO.getTopicId());
            }
        }
        article.setName(articleDTO.getName());
        article.setContent(articleDTO.getContent());
        article.setTopics(topics);
        Article updatedArticle = articleRepository.save(article);
        ArticleDTO updatedArticleDTO = modelMapper.map(updatedArticle,ArticleDTO.class);
        List<TopicDTO> topicsListDTO = new ArrayList<>();
        for(Topic topic : updatedArticle.getTopics()) {
            String username = topic.getUser().getUsername();
            TopicDTO topicDTO = modelMapper.map(topic, TopicDTO.class);
            topicDTO.setUsername(username);
            topicsListDTO.add(topicDTO);
        }
        updatedArticleDTO.setUsername(userDetails.getUsername());
        updatedArticleDTO.setTopics(topicsListDTO);

        return updatedArticleDTO;
    }

    @Override
    @Transactional
    public ArticleDTO submitArticle(Long articleId, @AuthenticationPrincipal UserDetails userDetails) {

        Article article = articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));
        boolean isJournalist = false;
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("JOURNALIST"))) {
            String articleUsername = userDetails.getUsername();
            User articleUser = userRepository.findByUsername(articleUsername);
            if (articleUser == null) {
                throw new AccessDeniedException("You are not authorized to update an article for another user.");
            }
            if (!userDetails.getUsername().equals(articleUser.getUsername())) {
                throw new AccessDeniedException("You are not authorized to create an article for another user.");
            }
            isJournalist = true;
        }
        if (article.getStatus() == ArticleStatus.PUBLISHED) {
            logger.warn("Article with ID {} is already published.", articleId);
            throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Article is already published");
        }
        if(isJournalist && article.getStatus() == ArticleStatus.SUBMITTED){
            throw new ArticleIsAlreadySubmittedException(HttpStatus.BAD_REQUEST, "Article with with ID: " + article.getArticleId() + " is already submitted!");
        }
        article.setStatus(ArticleStatus.SUBMITTED);
        logger.info("Article with ID {} submitted successfully.", articleId);
        articleRepository.save(article);
        return modelMapper.map(article,ArticleDTO.class);
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
    public ArticleDTO rejectArticle(Long id, RejectArticleRequest rejectArticleRequest) {
        // Get the article by ID from the database
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Article", "id", id));

        if (article.getStatus() != ArticleStatus.SUBMITTED) {
            throw new NewsAPIException(HttpStatus.BAD_REQUEST, "This article is not in create state");
        }
        // Set the rejection reason
        article.setRejectionReason(rejectArticleRequest.getRejectionReason());
        // Set the status to REJECTED
        article.setStatus(ArticleStatus.REJECTED);
        // Save the rejected article
        articleRepository.save(article);
        logger.info("Article with ID {} has been rejected with reason: {}", id, rejectArticleRequest.getRejectionReason());
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
    public List<ArticleDTO> searchArticlesWithFilters(
            UserDetails userDetails,
            String name,
            String content
    ) {
        List<Article> results = new ArrayList<>();

        if (userDetails != null) {
            if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("JOURNALIST"))) {
                User journalistUser = userRepository.findByUsername(userDetails.getUsername());
                results = filterArticles(journalistUser.getId(), name, content);
            } else if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("EDITOR")) || userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
                results = filterArticles(null, name, content);
            } else {
                throw new AccessDeniedException("Bad request");
            }
        } else {
            results = filterArticles(null, name, content);
        }

        List<ArticleDTO> resultsDTO = results.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());

        return resultsDTO;
    }

    private List<Article> filterArticles(Long userId, String name, String content) {
        if (userId != null) {
            if (name != null && content != null) {
                return articleRepository.findByNameContainingAndContentContainingAndUserId(name, content, userId);
            } else if (name != null) {
                return articleRepository.findByNameContainingAndUserId(name, userId);
            } else if (content != null) {
                return articleRepository.findByContentContainingAndUserId(content, userId);
            } else {
                return articleRepository.findByUserId(userId);
            }
        } else {
            if (name != null && content != null) {
                return articleRepository.findByNameContainingAndContentContaining(name, content);
            } else if (name != null) {
                return (List<Article>) articleRepository.findByNameContaining(name);
            } else if (content != null) {
                return articleRepository.findByContentContaining(content);
            } else {
                return articleRepository.findAll();
            }
        }
    }

    public Article getArticleByName(String name) {
        return articleRepository.findByName(name);
    }

    @Override
    public ArticleDTO getArticleById(Long articleId) {
        Article article =  articleRepository.findById(articleId).orElseThrow(() -> new ResourceNotFoundException("Post", "id", articleId));
        ArticleDTO articleDTO = modelMapper.map(article,ArticleDTO.class);
        return articleDTO;
    }

    private ArticleDTO mapToDTO(Article article){
        ArticleDTO postDto = modelMapper.map(article, ArticleDTO.class);
        return postDto;
    }

    private Article mapToEntity(ArticleDTO articleDTO){
        Article post = modelMapper.map(articleDTO, Article.class);
        return post;
    }

    @Override
    public List<ArticleDTO> getArticlesWithFilters(
            UserDetails userDetails,
            ArticleStatus status,
            LocalDate startDate,
            LocalDate endDate
    ) {
        List<Article> allArticles = new ArrayList<>();

        if (userDetails != null) {
            if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("JOURNALIST"))) {
                User journalistUser = userRepository.findByUsername(userDetails.getUsername());
                allArticles = filterArticlesForJournalist(journalistUser.getId(), status, startDate, endDate);
            } else if (userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("EDITOR")) || userDetails.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {
                allArticles = filterArticlesForEditorOrAdmin(status, startDate, endDate);
            } else {
                throw new AccessDeniedException("Bad request");
            }
        } else {
            allArticles = filterArticlesForUnauthenticatedUser(status, startDate, endDate);
        }

        List<ArticleDTO> allArticlesDTO = allArticles.stream()
                .map(article -> modelMapper.map(article, ArticleDTO.class))
                .collect(Collectors.toList());

        return allArticlesDTO;
    }

    private List<Article> filterArticlesForJournalist(Long journalistUserId, ArticleStatus status, LocalDate startDate, LocalDate endDate) {
        if (status != null && startDate != null && endDate != null) {
            return articleRepository.findByUserIdAndStatusAndCreatedAtBetweenOrUserIdAndStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
                    journalistUserId, status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    journalistUserId, status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else if (status != null) {
            return articleRepository.findByUserIdAndStatusOrderByStatusDescCreatedAtDesc(journalistUserId, status);
        } else if (startDate != null && endDate != null) {
            return articleRepository.findByUserIdAndCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
                    journalistUserId, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else {
            return articleRepository.findByUserIdOrderByStatusDescCreatedAtDesc(journalistUserId);
        }
    }

    private List<Article> filterArticlesForEditorOrAdmin(ArticleStatus status, LocalDate startDate, LocalDate endDate) {
        if (status != null && startDate != null && endDate != null) {
            return articleRepository.findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else if (status != null) {
            return articleRepository.findByStatusOrderByStatusDescCreatedAtDesc(status);
        } else if (startDate != null && endDate != null) {
            return articleRepository.findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else {
            return articleRepository.findAllByOrderByStatusDescCreatedAtDesc();
        }
    }

    private List<Article> filterArticlesForUnauthenticatedUser(ArticleStatus status, LocalDate startDate, LocalDate endDate) {
        if (status != null && startDate != null && endDate != null) {
            return articleRepository.findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX),
                    status, startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else if (status != null) {
            return articleRepository.findByStatusOrderByStatusDescCreatedAtDesc(status);
        } else if (startDate != null && endDate != null) {
            return articleRepository.findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(
                    startDate.atStartOfDay(), endDate.atTime(LocalTime.MAX));
        } else {
            return articleRepository.findAllByStatusOrderByStatusDescCreatedAtDesc(ArticleStatus.PUBLISHED);
        }
    }

    @Override
    public List<ArticleDTO> getArticles(@AuthenticationPrincipal UserDetails userDetails){
        List<Article> allArticles = articleRepository.findAllByStatus(ArticleStatus.PUBLISHED);
        List<ArticleDTO> allArticlesDTO = new ArrayList<>();
        if (userDetails != null) {
            if(userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("JOURNALIST"))) {
            User journalistUser = userRepository.findByUsername(userDetails.getUsername());
            allArticles = articleRepository.findAllByStatus(ArticleStatus.PUBLISHED);
            allArticles.addAll(articleRepository.findByIdAAndNotStatus(journalistUser.getId(), ArticleStatus.PUBLISHED));
            allArticlesDTO = new ArrayList<>();
            for (Article article : allArticles) {
                allArticlesDTO.add(modelMapper.map(article, ArticleDTO.class));
            }
            return allArticlesDTO;
        } else if (userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("EDITOR")) || userDetails.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ADMIN"))) {

            allArticles = articleRepository.findAllByStatus(ArticleStatus.PUBLISHED);

            allArticlesDTO = new ArrayList<>();
            for (Article article : allArticles) {
                allArticlesDTO.add(modelMapper.map(article, ArticleDTO.class));
            }
            return allArticlesDTO;

        }else {
                throw new AccessDeniedException("Bad request");
            }
        } else  {
            allArticles = articleRepository.findAllByStatus(ArticleStatus.PUBLISHED);
            for (Article article : allArticles) {
                allArticlesDTO.add(modelMapper.map(article, ArticleDTO.class));
            }
            return allArticlesDTO;
        }

    }

}



