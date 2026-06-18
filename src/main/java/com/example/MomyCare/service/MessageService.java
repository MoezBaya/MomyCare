package com.example.MomyCare.service;


import com.example.MomyCare.dto.message.MessageRequest;
import com.example.MomyCare.dto.message.MessageResponse;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(Authentication auth, MessageRequest request);

    List<MessageResponse> getConversation(Authentication auth, Long userId);
}
