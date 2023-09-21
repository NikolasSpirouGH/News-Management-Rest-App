package com.news.controller;

import com.news.entity.Topic;
import com.news.payload.TopicDTO;
import com.news.service.TopicService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/topics")
@RequiredArgsConstructor
public class TopicController {

    private final TopicService topicService;

    @PreAuthorize("hasAnyAuthority('JOURNALIST','EDITOR','ADMIN')")
    @PostMapping("/createTopic")
    public ResponseEntity<TopicDTO> saveTopic(@Valid @RequestBody TopicDTO topicDTO, @AuthenticationPrincipal UserDetails userDetails) {
        TopicDTO topic = topicService.createTopic(topicDTO,userDetails);
        return new ResponseEntity<>(topic, HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyAuthority('JOURNALIST','ADMIN','EDITOR')")
    @GetMapping("/readAll")
    public ResponseEntity<List<Topic>> readAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','EDITOR')")
    @PutMapping("/updateTopic/{id}")
    public ResponseEntity<TopicDTO> updateTopic(@Valid @RequestBody TopicDTO topicDTO) {
        topicService.updateTopic(topicDTO);
        return new ResponseEntity<>(topicDTO, HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','EDITOR')")
    @PutMapping("/approveTopic/{id}")
    public ResponseEntity<String> approveTopic(Long id) {
        topicService.approveTopic(id);
        return new ResponseEntity<>("Topic approved", HttpStatus.OK);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','EDITOR')")
    @PutMapping("/rejectTopic")
    public ResponseEntity<String> rejectTopic(Long id) {
        topicService.rejectTopic(id);
        return new ResponseEntity<>("Topic Rejected", HttpStatus.OK);
    }

}
