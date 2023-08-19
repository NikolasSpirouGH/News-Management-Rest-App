package com.web.topic.service;

import com.web.topic.model.Topic;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {


   public Topic saveTopic(Topic topic);

   List<Topic> getAllTopics();

   Topic getTopicByName(String name);

}
