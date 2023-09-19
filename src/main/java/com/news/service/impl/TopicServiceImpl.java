package com.news.service.impl;

import com.news.entity.Topic;
import com.news.entity.User;
import com.news.exception.ResourceNotFoundException;
import com.news.repository.TopicRepository;
import com.news.repository.UserRepository;
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

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    @Override
    public TopicDTO createTopic(TopicDTO topicDTO) {
        User user = userRepository.findByUsername(topicDTO.getUsername());

        System.out.println(topicDTO.getUsername());

        if(user.getUsername() == null) {
            throw new ResourceNotFoundException("topic","id",topicDTO.getTopicId());
        }

        Topic topic = modelMapper.map(topicDTO, Topic.class);
        topic.setUser(user);
        Topic savedTopic = topicRepository.save(topic);
        //String username = modelMapper.map(topic.getUser().getUsername(),String.class);
        String username = topic.getUser().getUsername();
        TopicDTO resultTopic = modelMapper.map(savedTopic,TopicDTO.class);
        resultTopic.setUsername(username);
        System.out.println("In topic: " + " " + resultTopic.getUsername());
        return resultTopic;
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
