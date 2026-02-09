package com.forumhub.dto;

public record RegisterResponse(
        Long id,
        String login,
        String nome,
        String email,
        String message
) {}