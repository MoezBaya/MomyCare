package com.example.MomyCare.dto.message;

import java.time.LocalDateTime;

public record MessageResponse(
        Long id,
        String content,
        Long senderId,
        Long receiverId,
        LocalDateTime sentAt
) {}
