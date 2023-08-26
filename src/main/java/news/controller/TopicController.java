package news.controller;

import news.entity.Topic;
import news.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/topics")
public class TopicController {

    @Autowired
    private TopicService topicService;

    @PostMapping("/createTopic")
    public ResponseEntity<Topic> saveTopic(@Valid @RequestBody Topic topic) {
         return new ResponseEntity<Topic> (topicService.saveTopic(topic), HttpStatus.CREATED);
    }

    @GetMapping("/readAll")
    public ResponseEntity<List<Topic>> readAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

}
