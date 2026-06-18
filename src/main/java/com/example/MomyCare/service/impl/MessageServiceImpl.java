package com.example.MomyCare.service.impl;

import com.example.MomyCare.dto.message.MessageRequest;
import com.example.MomyCare.dto.message.MessageResponse;
import com.example.MomyCare.model.Message;
import com.example.MomyCare.model.User;
import com.example.MomyCare.dao.MessageRepository;
import com.example.MomyCare.dao.UserRepository;
import com.example.MomyCare.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public MessageResponse sendMessage(Authentication auth, MessageRequest request) {

        User sender = userRepository.findByLogin(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new RuntimeException("Receiver not found"));

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .receiver(receiver)
                .sentAt(LocalDateTime.now())
                .build();

        messageRepository.save(message);

        return new MessageResponse(
                message.getId(),
                message.getContent(),
                sender.getId(),
                receiver.getId(),
                message.getSentAt()
        );
    }

    @Override
    public List<MessageResponse> getConversation(Authentication auth, Long userId) {

        User currentUser = userRepository.findByLogin(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        User otherUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Message> messages = messageRepository
                .findBySenderAndReceiverOrReceiverAndSender(
                        currentUser, otherUser,
                        currentUser, otherUser
                );

        return messages.stream().map(m ->
                new MessageResponse(
                        m.getId(),
                        m.getContent(),
                        m.getSender().getId(),
                        m.getReceiver().getId(),
                        m.getSentAt()
                )
        ).toList();
    }
}
