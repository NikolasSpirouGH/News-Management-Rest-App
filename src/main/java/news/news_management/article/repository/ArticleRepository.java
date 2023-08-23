package news.news_management.article.repository;

import news.news_management.article.model.Article;
import news.news_management.article.model.ArticleStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long>{

    List<Article> findByNameContainingAndContentContaining(String name, String content);

    Article findByName(String name);

    List<Article> findByContent(String content);

    List<Article> findByStatusOrderByStatusDescCreatedAtDesc(ArticleStatus status);

    List<Article> findByCreatedAtBetweenOrderByStatusDescCreatedAtDesc(LocalDateTime localDateTime, LocalDateTime localDateTime1);

    List<Article> findAllByOrderByStatusDescCreatedAtDesc();

    List<Article> findByStatusAndCreatedAtBetweenOrStatusAndPublishedAtBetweenOrderByStatusDescCreatedAtDesc(ArticleStatus status, LocalDateTime localDateTime, LocalDateTime localDateTime1, ArticleStatus status1, LocalDateTime localDateTime2, LocalDateTime localDateTime3);

}
