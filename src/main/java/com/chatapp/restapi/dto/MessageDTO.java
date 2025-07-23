package com.chatapp.restapi.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private String username;
    private String content;
    private LocalDateTime createdAt;
}
