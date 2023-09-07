package news.article.service;

import com.news.repository.ArticleRepository;
import com.news.repository.TopicRepository;
import com.news.service.impl.ArticleServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

@SpringBootTest
@SpringJUnitConfig

public class ArticleServiceImplTest {

    @Autowired
    private ArticleServiceImpl articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private TopicRepository topicRepository;

//    @Test
//    public void testCreateArticle_Success() {
//        // Arrange
//        TopicDTO topicDTO1 = new TopicDTO(1L, "Technology", "IT");
//        TopicDTO topicDTO2 = new TopicDTO(2L, "Physics", "Science");
//
//        List<TopicDTO> topicsDTO = new ArrayList<>();
//        topicsDTO.add(topicDTO1);
//        topicsDTO.add(topicDTO2);
//
//        ArticleDTO articleDTO = new ArticleDTO(/* initialize with required data */);
//
//        Topic topic1 = new Topic(1L, "Technology", "IT");
//        Topic topic2 = new Topic(2L, "Physics", "Science");
//
//        // Stub the findByName method to return the topics when called
//        when(topicRepository.findByName("Technology")).thenReturn(topic1);
//        when(topicRepository.findByName("Science")).thenReturn(topic2);
//
//        // Act
//        ArticleDTO createdArticle = articleService.createArticle(articleDTO);
//
//        // Assert
//        assertNotNull(createdArticle);
//        assertEquals(article., createdArticle.getArticleStatus());
//
//        // Verify that the save method was called with the expected entity
//        verify(articleRepository, times(1)).save(any(Article.class));
//    }
//    @Test
//    public void testUpdateArticle_Success() {
//        // Create a test ArticleRequest and TopicRequest
//        ArticleDTO articleDTO = new ArticleDTO();
//        articleDTO.setName("Updated Article");
//        articleDTO.setContent("Updated Content");
//
//        TopicRequest topicRequest1 = new TopicRequest();
//        topicRequest1.setName("Topic1");
//        topicRequest1.setFathersName("ParentTopic"); // Assuming a parent topic name
//
//        List<TopicRequest> topicRequests = new ArrayList<>();
//        topicRequests.add(topicRequest1);
//        articleDTO.setTopics(topicRequests);
//
//        // Create a test Article
//        Article existingArticle = new Article();
//        existingArticle.setArticleId(1L);
//        existingArticle.setName("Old Article");
//        existingArticle.setContent("Old Content");
//
//        // Create a test Topic
//        Topic existingTopic = new Topic();
//        existingTopic.setTopicId(1L);
//        existingTopic.setName("Topic1");
//
//        // Set up mock behavior
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(existingArticle));
//        when(topicService.getTopicByName("Topic1")).thenReturn(existingTopic);
//        when(articleRepository.save(existingArticle)).thenReturn(existingArticle);
//
//        // Call the method to be tested
//        Article updatedArticle = articleService.updateArticle(1L, articleDTO);
//
//        // Assertions or verifications
//        verify(articleRepository, times(1)).findById(1L);
//        verify(topicService, times(1)).getTopicByName("Topic1");
//        verify(articleRepository, times(1)).save(existingArticle);
//        assertEquals("Updated Article", updatedArticle.getName());
//        assertEquals("Updated Content", updatedArticle.getContent());
//        // Verify other assertions as needed
//    }
}
