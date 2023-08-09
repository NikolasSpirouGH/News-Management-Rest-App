package com.web.topic.service;

import com.web.topic.dto.TopicRequest;
import com.web.topic.model.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {
    Topic createTopic(TopicRequest req);

    List<Topic> getAllTopics();


    Topic getOrCreateTopicByName(String topicName);



}
