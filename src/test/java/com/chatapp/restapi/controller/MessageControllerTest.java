package com.chatapp.restapi.controller;

import com.chatapp.restapi.dto.MessageDTO;
import com.chatapp.restapi.entity.User;
import com.chatapp.restapi.repository.MessageRepository;
import com.chatapp.restapi.repository.UserRepository;
import com.chatapp.restapi.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import org.springframework.http.*;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MessageControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String baseUrl() {
        return "http://localhost:" + port + "/messages";
    }

    private String obtainJwtToken(String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", username);
        loginRequest.put("password", password);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity("http://localhost:" + port + "/auth/login", entity, Map.class);
        return (String) response.getBody().get("token");
    }

    @BeforeEach
    void setUp() {
        if (userRepository.findByUsername("testuser").isEmpty()) {
            User user = User.builder()
                    .username("testuser")
                    .password(passwordEncoder.encode("password"))
                    .role("ROLE_USER")
                    .build();
            userRepository.save(user);
        }
    }

    @Test
    void testPostMessage_shouldReturn201() {
        String token = obtainJwtToken("testuser", "password");
        System.out.println("JWT token used in test: " + token);

        MessageDTO request = new MessageDTO();
        request.setContent("Hello integration test");
        request.setUsername("testuser");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);
        System.out.println("Authorization header: " + headers.getFirst(HttpHeaders.AUTHORIZATION));

        HttpEntity<MessageDTO> httpEntity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl(), httpEntity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(messageRepository.findAll()).anyMatch(m -> m.getContent().equals("Hello integration test"));
    }

}
