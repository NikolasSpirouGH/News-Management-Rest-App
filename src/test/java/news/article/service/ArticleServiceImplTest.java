package news.article.service;

import news.dto.ArticleRequest;
import news.entity.Article;
import news.repository.ArticleRepository;
import news.dto.TopicRequest;
import news.entity.Topic;
import news.service.TopicService;
import news.service.ArticleServiceImpl;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;

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

        Topic existingTopicFootball= new Topic();
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
}

