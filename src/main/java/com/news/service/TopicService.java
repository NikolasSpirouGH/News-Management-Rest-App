package com.news.service;

import com.news.entity.Topic;
import com.news.payload.TopicDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TopicService {

   public TopicDTO createTopic(TopicDTO topic, @AuthenticationPrincipal UserDetails userDetails);

   List<Topic> getAllTopics();

   Topic getTopicByName(String name);

   TopicDTO updateTopic(TopicDTO topicDTO);

   TopicDTO approveTopic(Long topicID);

   ResponseEntity.BodyBuilder rejectTopic(Long topicID);

}
