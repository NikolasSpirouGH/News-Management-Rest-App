package news.news_management.comment.controller;

import news.news_management.article.model.Article;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentControllerTest {

    @BeforeEach
    void setUp() {
        Article article = new Article();
        article.setName("");
    }

    @Test
public void testCreateArticleEndpoint() {

    }
}