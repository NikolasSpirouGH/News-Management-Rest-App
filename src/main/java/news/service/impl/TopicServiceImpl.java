package news.service.impl;

import lombok.RequiredArgsConstructor;
import news.entity.Topic;
import news.payload.TopicDTO;
import news.repository.TopicRepository;
import news.service.TopicService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
