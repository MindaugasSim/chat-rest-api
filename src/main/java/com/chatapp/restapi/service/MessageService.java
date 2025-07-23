package com.chatapp.restapi.service;

import com.chatapp.restapi.dto.MessageDTO;
import com.chatapp.restapi.entity.Message;
import com.chatapp.restapi.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import com.chatapp.restapi.entity.User;
import java.util.Map;

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

    public void anonymizeMessages(User from, User to) {
        List<Message> messages = messageRepository.findByUserId(from.getId());
        for (Message message : messages) {
            message.setUser(to);
        }
        messageRepository.saveAll(messages);
        messageRepository.flush();
    }

    public List<Map<String, Object>> getUserStatistics() {
        List<Object[]> rows = messageRepository.getUserStatisticsNative();
        List<Map<String, Object>> stats = new java.util.ArrayList<>();
        for (Object[] row : rows) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("username", row[0]);
            map.put("message_count", row[1]);
            map.put("first_message_time", row[2]);
            map.put("last_message_time", row[3]);
            map.put("avg_message_length", row[4]);
            map.put("last_message_text", row[5]);
            stats.add(map);
        }
        return stats;
    }
}
