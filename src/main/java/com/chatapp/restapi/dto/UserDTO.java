// src/main/java/com/chatapp/restapi/dto/UserDTO.java
package com.chatapp.restapi.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
}
