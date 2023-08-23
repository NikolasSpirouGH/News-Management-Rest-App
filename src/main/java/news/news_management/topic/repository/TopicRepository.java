package news.news_management.topic.repository;


import news.news_management.topic.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic,Long> {
    Topic findByName(String name);
}
