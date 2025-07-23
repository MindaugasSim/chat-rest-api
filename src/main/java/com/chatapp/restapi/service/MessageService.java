package com.chatapp.restapi.service;

import com.chatapp.restapi.dto.MessageDTO;
import com.chatapp.restapi.entity.Message;
import com.chatapp.restapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    public void sendMessage(Message message) {
        messageRepository.save(message);
    }

    public List<MessageDTO> getAllMessages() {
        return messageRepository.findAllOrderedByNewest()
                .stream()
                .map(msg -> new MessageDTO(
                        msg.getUser().getUsername(),
                        msg.getContent(),
                        msg.getCreatedAt()))
                .toList();
    }
}
