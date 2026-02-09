package com.forumhub.service;

import com.forumhub.dto.TopicCreateRequest;
import com.forumhub.dto.TopicUpdateRequest;
import com.forumhub.model.Course;
import com.forumhub.model.Topic;
import com.forumhub.model.User;
import com.forumhub.repository.CourseRepository;
import com.forumhub.repository.TopicRepository;
import com.forumhub.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TopicService {
    
    @Autowired
    private TopicRepository topicRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Transactional
    public Topic createTopic(TopicCreateRequest request, String username) {
        // Validar tópico duplicado
        if (topicRepository.existsByTituloAndMensagem(request.titulo(), request.mensagem())) {
            throw new IllegalArgumentException("Já existe um tópico com este título e mensagem");
        }
        
        // Buscar usuário autor
        User autor = userRepository.findUserByLogin(username)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
        
        // Buscar ou criar curso
        Course curso = courseRepository.findByNomeIgnoreCase(request.nomeCurso())
                .orElseGet(() -> {
                    Course newCourse = new Course();
                    newCourse.setNome(request.nomeCurso());
                    newCourse.setCategoria("Programação");
                    return courseRepository.save(newCourse);
                });
        
        // Criar tópico
        Topic topic = new Topic();
        topic.setTitulo(request.titulo());
        topic.setMensagem(request.mensagem());
        topic.setAutor(autor);
        topic.setCurso(curso);
        
        return topicRepository.save(topic);
    }
    
    public List<Topic> getAllTopics() {
        return topicRepository.findAll();
    }
    
    public Topic getTopicById(Long id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));
    }
    
    @Transactional
    public Topic updateTopic(Long id, TopicUpdateRequest request, String username) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));
        
        // Verificar se usuário é o autor
        if (!topic.getAutor().getLogin().equals(username)) {
            throw new IllegalArgumentException("Apenas o autor pode atualizar o tópico");
        }
        
        // Atualizar campos
        if (request.titulo() != null && !request.titulo().isBlank()) {
            topic.setTitulo(request.titulo());
        }
        
        if (request.mensagem() != null && !request.mensagem().isBlank()) {
            topic.setMensagem(request.mensagem());
        }
        
        if (request.status() != null && !request.status().isBlank()) {
            try {
                Topic.StatusTopico newStatus = Topic.StatusTopico.valueOf(request.status().toUpperCase());
                topic.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Status inválido: " + request.status());
            }
        }
        
        return topicRepository.save(topic);
    }
    
    @Transactional
    public void deleteTopic(Long id, String username) {
        Topic topic = topicRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tópico não encontrado"));
        
        // Verificar se usuário é o autor
        if (!topic.getAutor().getLogin().equals(username)) {
            throw new IllegalArgumentException("Apenas o autor pode deletar o tópico");
        }
        
        topicRepository.delete(topic);
    }
}
