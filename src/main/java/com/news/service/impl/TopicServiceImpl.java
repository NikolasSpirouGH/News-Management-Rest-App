package com.news.service.impl;

import com.news.entity.Topic;
import com.news.entity.TopicStatus;
import com.news.entity.User;
import com.news.exception.NewsAPIException;
import com.news.exception.ResourceNotFoundException;
import com.news.repository.TopicRepository;
import com.news.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import com.news.payload.TopicDTO;
import com.news.service.TopicService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;
    private static final Logger logger = LoggerFactory.getLogger(TopicService.class);

    @Override
    public TopicDTO createTopic(TopicDTO topicDTO, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername());
        System.out.println(topicDTO.getUsername());
        if(user.getUsername() == null) {
            throw new ResourceNotFoundException("topic","id",topicDTO.getTopicId());
        }

        Topic topic = modelMapper.map(topicDTO, Topic.class);
        topic.setUser(user);
        topic.setStatus(TopicStatus.CREATED);
        Topic savedTopic = topicRepository.save(topic);
        String username = userDetails.getUsername();
        TopicDTO resultTopic = modelMapper.map(savedTopic,TopicDTO.class);
        resultTopic.setUsername(username);
        System.out.println("In topic: " + " " + resultTopic.getUsername());
        return resultTopic;
    }

    @Override
    public TopicDTO updateTopic(TopicDTO topicDTO){
        Topic topic = topicRepository.findById(topicDTO.getTopicId()).orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicDTO.getTopicId()));
        if(topic.getStatus() == TopicStatus.APPROVED){
            logger.warn("Topic with ID {} is already APPROVED.", topicDTO.getTopicId());
            throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Topic is not APPROVED!");
        }

        topic = modelMapper.map(topicDTO, Topic.class);
        topic = topicRepository.save(topic);
        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public TopicDTO approveTopic(Long topicID) {
        Topic topic = topicRepository.findById(topicID).orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicID));
        if(topic.getStatus() == TopicStatus.APPROVED){
            logger.warn("Topic with ID {} is already APPROVED.", topicID);
            throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Topic is already APPROVED");
        }
        topic.setStatus(TopicStatus.APPROVED);

        return modelMapper.map(topic, TopicDTO.class);
    }

    @Override
    public ResponseEntity.BodyBuilder rejectTopic(Long topicID) {
        Topic topic = topicRepository.findById(topicID).orElseThrow(() -> new ResourceNotFoundException("Topic", "id", topicID));
        if(topic.getStatus() == TopicStatus.CREATED){
            logger.warn("Topic with ID {} is already published.", topicID);
            throw new NewsAPIException( HttpStatus.BAD_REQUEST,"Topic is already REJECTED!");
        }

        topicRepository.delete(topic);

        return ResponseEntity.ok();
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
