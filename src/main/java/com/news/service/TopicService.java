package com.news.service;

import com.news.entity.Topic;
import com.news.payload.TopicDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {

   public TopicDTO createTopic(TopicDTO topic);

   List<Topic> getAllTopics();

   Topic getTopicByName(String name);

}
