package com.chatapp.restapi.mapper;

import com.chatapp.restapi.dto.MessageDTO;
import com.chatapp.restapi.entity.Message;
import com.chatapp.restapi.entity.User;

public class MessageMapper {

    public static MessageDTO toDto(Message entity) {
        if (entity == null) return null;
        return MessageDTO.builder()
                .username(entity.getUser().getUsername())
                .content(entity.getContent())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static Message toEntity(MessageDTO dto, User user) {
        if (dto == null) return null;
        return Message.builder()
                .user(user)
                .content(dto.getContent())
                .createdAt(dto.getCreatedAt())
                .build();
    }
}
