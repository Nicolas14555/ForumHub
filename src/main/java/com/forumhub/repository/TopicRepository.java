package com.forumhub.repository;

import com.forumhub.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    boolean existsByTituloAndMensagem(String titulo, String mensagem);
}
