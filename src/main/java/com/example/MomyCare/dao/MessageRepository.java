package com.example.MomyCare.dao;

import com.example.MomyCare.model.Message;
import com.example.MomyCare.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findBySenderAndReceiverOrReceiverAndSender(
            User sender1,
            User receiver1,
            User sender2,
            User receiver2
    );
}