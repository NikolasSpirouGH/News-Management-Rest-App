package news.service;

import news.entity.Topic;
import news.payload.TopicDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {

   public TopicDTO createTopic(TopicDTO topic);

   List<Topic> getAllTopics();

   Topic getTopicByName(String name);

}
