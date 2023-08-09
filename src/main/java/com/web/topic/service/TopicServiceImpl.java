package com.web.topic.service;

import com.web.topic.dto.TopicRequest;
import com.web.topic.model.Topic;
import com.web.topic.repository.TopicRepository;
import com.web.topic.service.TopicService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private final TopicRepository topicRepository;

    @Autowired
    public TopicServiceImpl(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Transactional
    @Override
    public Topic createTopic(TopicRequest req) {
        Topic topic = new Topic();
        topic.setName(req.getName());
        return topicRepository.save(topic);
    }

    public Topic getOrCreateTopicByName(String topicName) {
        Topic topic = topicRepository.findByName(topicName);

        if (topic == null) {
            topic = new Topic();
            topic.setName(topicName);
            topic = topicRepository.save(topic);
        }

        return topic;
    }

    @Override
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }


}
