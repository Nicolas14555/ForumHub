package com.forumhub.controller;

import com.forumhub.dto.TopicCreateRequest;
import com.forumhub.dto.TopicResponse;
import com.forumhub.dto.TopicUpdateRequest;
import com.forumhub.model.Topic;
import com.forumhub.service.TopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/topicos")
public class TopicController {
    
    @Autowired
    private TopicService topicService;
    
    /**
     * CREATE - Criar novo tópico (requer autenticação)
     */
    @PostMapping
    public ResponseEntity<TopicResponse> createTopic(
            @RequestBody @Valid TopicCreateRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        Topic topic = topicService.createTopic(request, username);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TopicResponse(topic));
    }
    
    /**
     * READ - Listar todos os tópicos (público)
     */
    @GetMapping
    public ResponseEntity<List<TopicResponse>> getAllTopics() {
        List<Topic> topics = topicService.getAllTopics();
        
        List<TopicResponse> response = topics.stream()
                .map(TopicResponse::new)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * READ - Buscar tópico por ID (público)
     */
    @GetMapping("/{id}")
    public ResponseEntity<TopicResponse> getTopicById(@PathVariable Long id) {
        Topic topic = topicService.getTopicById(id);
        return ResponseEntity.ok(new TopicResponse(topic));
    }
    
    /**
     * UPDATE - Atualizar tópico (requer autenticação e ser o autor)
     */
    @PutMapping("/{id}")
    public ResponseEntity<TopicResponse> updateTopic(
            @PathVariable Long id,
            @RequestBody TopicUpdateRequest request,
            Authentication authentication) {
        
        String username = authentication.getName();
        Topic topic = topicService.updateTopic(id, request, username);
        
        return ResponseEntity.ok(new TopicResponse(topic));
    }
    
    /**
     * DELETE - Deletar tópico (requer autenticação e ser o autor)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTopic(
            @PathVariable Long id,
            Authentication authentication) {
        
        String username = authentication.getName();
        topicService.deleteTopic(id, username);
        
        return ResponseEntity.ok().build();
    }
}
