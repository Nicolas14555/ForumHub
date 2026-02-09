package com.forumhub.dto;

import com.forumhub.model.Topic;

import java.time.LocalDateTime;

public record TopicResponse(
    Long id,
    String titulo,
    String mensagem,
    LocalDateTime dataCriacao,
    String status,
    String autor,
    String curso
) {
    public TopicResponse(Topic topic) {
        this(
            topic.getId(),
            topic.getTitulo(),
            topic.getMensagem(),
            topic.getDataCriacao(),
            topic.getStatus().toString(),
            topic.getAutor().getNome(),
            topic.getCurso().getNome()
        );
    }
}
