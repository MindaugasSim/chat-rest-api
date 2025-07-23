package com.chatapp.restapi.mapper;

import com.chatapp.restapi.dto.UserDTO;
import com.chatapp.restapi.entity.User;

public class UserMapper {

    public static UserDTO toDto(User entity) {
        if (entity == null) return null;
        return UserDTO.builder()
                .id(entity.getId())
                .username(entity.getUsername())
                .role(entity.getRole())
                .build();
    }

    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        return User.builder()
                .id(dto.getId())
                .username(dto.getUsername())
                .role(dto.getRole())
                .build();
    }
}
