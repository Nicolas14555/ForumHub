package com.forumhub.dto;

public record TopicUpdateRequest(
    String titulo,
    String mensagem,
    String status
) {}
