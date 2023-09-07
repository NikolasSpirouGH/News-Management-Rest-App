package com.news.service.impl;

import com.news.entity.Topic;
import com.news.repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import com.news.payload.TopicDTO;
import com.news.service.TopicService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TopicServiceImpl implements TopicService {

    private final TopicRepository topicRepository;

    private final ModelMapper modelMapper;

    @Override
    public TopicDTO createTopic(TopicDTO topicDTO) {
        Topic topic = modelMapper.map(topicDTO, Topic.class);
        Topic savedTopic = topicRepository.save(topic);
        return modelMapper.map(savedTopic, TopicDTO.class);
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
