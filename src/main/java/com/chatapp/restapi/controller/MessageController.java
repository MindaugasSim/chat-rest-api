package com.chatapp.restapi.controller;

import com.chatapp.restapi.dto.MessageDTO;
import com.chatapp.restapi.repository.UserRepository;
import com.chatapp.restapi.entity.Message;
import com.chatapp.restapi.entity.User;
import com.chatapp.restapi.mapper.MessageMapper;
import com.chatapp.restapi.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/messages")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<MessageDTO>> getMessages() {
        return ResponseEntity.ok(messageService.getAllMessages());
    }

    @PostMapping
    public ResponseEntity<Void> sendMessage(@RequestBody MessageDTO dto) {
        Optional<User> optionalUser = userRepository.findByUsername(dto.getUsername());
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user = optionalUser.get();

        Message message = MessageMapper.toEntity(dto, user);
        messageService.sendMessage(message);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}

