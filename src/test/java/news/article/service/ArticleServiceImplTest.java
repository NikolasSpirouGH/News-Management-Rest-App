package news.article.service;

import news.dto.ArticleRequest;
import news.entity.Article;
import news.repository.ArticleRepository;
import news.dto.TopicRequest;
import news.entity.Topic;
import news.service.TopicService;
import news.service.ArticleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@SpringJUnitConfig
public class ArticleServiceImplTest {

    @InjectMocks
    private ArticleServiceImpl articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private TopicService topicService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateArticle_Success() {
        // Prepare test data
        ArticleRequest request = new ArticleRequest();

        TopicRequest topicRequest1 = new TopicRequest();
        TopicRequest topicRequest2 = new TopicRequest();
        topicRequest1.setName("Basketball");
        topicRequest2.setName("Football");

        List<TopicRequest> topicRequests = new ArrayList<>();
        topicRequests.add(topicRequest1);
        topicRequests.add(topicRequest2);

        request.setName("Sample Article");
        request.setContent("Sample Content");
        request.setTopics(topicRequests);

        // Set up mocks
        when(articleRepository.findByName("SampleArticle")).thenReturn(null); // No existing article

        // Mock the behavior for each topic requested
        Topic existingTopicBasketball = new Topic();
        when(topicService.getTopicByName("Basketball")).thenReturn(existingTopicBasketball);

        Topic existingTopicFootball = new Topic();
        when(topicService.getTopicByName("Football")).thenReturn(existingTopicFootball);

        // Create an Article object to be returned from the repository's save method
        Article savedArticle = new Article();
        savedArticle.setArticleId(1L);
        savedArticle.setName(request.getName());
        savedArticle.setContent(request.getContent());

        // Set up topics for savedArticle
        List<Topic> savedTopics = new ArrayList<>();
        savedTopics.add(existingTopicFootball);
        savedTopics.add(existingTopicBasketball);
        savedArticle.setTopics(savedTopics);

        when(articleRepository.save(eq(savedArticle))).thenReturn(savedArticle);

        // Call the method being tested
        Article createdArticle = articleService.createArticle(request);

        // Assert the behavior or outcomes
        System.out.println("SAVED ARTICLE " + savedArticle.getArticleId());
        System.out.println("CREATED ARTICLE " + createdArticle.getArticleId());
        assertNotNull(createdArticle);
        assertEquals(savedArticle.getName(), createdArticle.getName());
        assertEquals(savedArticle.getContent(), createdArticle.getContent());
        assertEquals(savedArticle.getTopics(), createdArticle.getTopics());

    }

    @Test
    public void testUpdateArticle_Success() {
        // Create a test ArticleRequest and TopicRequest
        ArticleRequest articleRequest = new ArticleRequest();
        articleRequest.setName("Updated Article");
        articleRequest.setContent("Updated Content");

        TopicRequest topicRequest1 = new TopicRequest();
        topicRequest1.setName("Topic1");
        topicRequest1.setFathersName("ParentTopic"); // Assuming a parent topic name

        List<TopicRequest> topicRequests = new ArrayList<>();
        topicRequests.add(topicRequest1);
        articleRequest.setTopics(topicRequests);

        // Create a test Article
        Article existingArticle = new Article();
        existingArticle.setArticleId(1L);
        existingArticle.setName("Old Article");
        existingArticle.setContent("Old Content");

        // Create a test Topic
        Topic existingTopic = new Topic();
        existingTopic.setTopicId(1L);
        existingTopic.setName("Topic1");

        // Set up mock behavior
        when(articleRepository.findById(1L)).thenReturn(Optional.of(existingArticle));
        when(topicService.getTopicByName("Topic1")).thenReturn(existingTopic);
        when(articleRepository.save(existingArticle)).thenReturn(existingArticle);

        // Call the method to be tested
        Article updatedArticle = articleService.updateArticle(1L, articleRequest);

        // Assertions or verifications
        verify(articleRepository, times(1)).findById(1L);
        verify(topicService, times(1)).getTopicByName("Topic1");
        verify(articleRepository, times(1)).save(existingArticle);
        assertEquals("Updated Article", updatedArticle.getName());
        assertEquals("Updated Content", updatedArticle.getContent());
        // Verify other assertions as needed
    }
}
