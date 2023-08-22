package news.news_management.topic.service;

import news.news_management.topic.model.Topic;
import news.news_management.topic.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicRepository topicRepository;


    @Override
    public Topic saveTopic(Topic topic) {
        return topicRepository.save(topic);
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }

    @Override
    public Topic getTopicByName(String name) {
        return topicRepository.findByName(name);
    }


}
