package com.example.MomyCare.controller;

import com.example.MomyCare.dto.message.MessageRequest;
import com.example.MomyCare.dto.message.MessageResponse;
import com.example.MomyCare.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<MessageResponse> send(
            Authentication auth,
            @RequestBody MessageRequest request
    ) {
        return ResponseEntity.ok(messageService.sendMessage(auth, request));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<MessageResponse>> conversation(
            Authentication auth,
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(messageService.getConversation(auth, userId));
    }
}
