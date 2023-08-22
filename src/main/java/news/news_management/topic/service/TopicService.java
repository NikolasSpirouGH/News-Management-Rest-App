package news.news_management.topic.service;

import news.news_management.topic.model.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {


   public Topic saveTopic(Topic topic);

   List<Topic> getAllTopics();

   Topic getTopicByName(String name);

}
