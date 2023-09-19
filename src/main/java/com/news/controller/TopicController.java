package com.news.controller;

import com.news.entity.Topic;
import com.news.payload.CommentDTO;
import com.news.payload.TopicDTO;
import com.news.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PostMapping("/createTopic")
    public ResponseEntity<TopicDTO> saveTopic(@Valid @RequestBody TopicDTO topicDTO) {
        TopicDTO topic = topicService.createTopic(topicDTO);
         return new ResponseEntity<>(topic, HttpStatus.CREATED);
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<Topic>> readAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }
}
