package com.chatapp.restapi.controller;

import com.chatapp.restapi.entity.CustomUserDetails;
import com.chatapp.restapi.entity.Message;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AdminControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String adminToken;

    @BeforeEach
    void setup() {
        messageRepository.deleteAll();
        userRepository.deleteAll();

        User admin = userRepository.save(User.builder()
                .username("admin")
                .password(passwordEncoder.encode("pass"))
                .role("ROLE_ADMIN")
                .build());

        System.out.println("Admin user in DB after save: " + userRepository.findByUsername("admin").get().getRole());

        CustomUserDetails details = new CustomUserDetails(admin);
        adminToken = jwtService.generateToken(details);
    }

    @Test
    void shouldRegisterUser() {
        String url = "http://localhost:" + port + "/admin/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(
                Map.of("username", "newuser", "password", "newpass"),
                headers
        );

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByUsername("newuser")).isPresent();
    }

    @Test
    void shouldDeleteUserAndAnonymizeMessages() {
        User victim = userRepository.save(User.builder()
                .username("victim")
                .password("pass")
                .role("ROLE_USER")
                .build());

        messageRepository.save(Message.builder()
                .content("Sensitive message")
                .user(victim)
                .build());

        userRepository.findByUsername("anonymous").orElseGet(() ->
                userRepository.save(User.builder()
                        .username("anonymous")
                        .password("")
                        .role("ROLE_USER")
                        .build()));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/admin/users/victim";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.DELETE, request, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(userRepository.findByUsername("victim")).isEmpty();

        List<Message> messages = messageRepository.findAll();
        assertThat(messages).isNotEmpty();
        assertThat(messages.get(0).getUser().getUsername()).isEqualTo("anonymous");
    }

    @Test
    void shouldReturnUserStatistics() {
        User user1 = userRepository.save(User.builder()
                .username("alice")
                .password("pass")
                .role("ROLE_USER")
                .build());

        User user2 = userRepository.save(User.builder()
                .username("bob")
                .password("pass")
                .role("ROLE_USER")
                .build());

        messageRepository.saveAll(List.of(
                Message.builder().content("msg1").user(user1).build(),
                Message.builder().content("msg2").user(user1).build(),
                Message.builder().content("msg3").user(user2).build()
        ));

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        String url = "http://localhost:" + port + "/admin/statistics";
        ResponseEntity<List> response = restTemplate.exchange(url, HttpMethod.GET, request, List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<Map<String, Object>> stats = response.getBody();

        assertThat(stats).isNotNull();
        assertThat(stats)
                .anySatisfy(stat -> {
                    assertThat(stat.get("username")).isEqualTo("alice");
                    assertThat(stat.get("message_count")).isEqualTo(2);
                    assertThat(stat.get("last_message_text")).isInstanceOf(String.class);
                })
                .anySatisfy(stat -> {
                    assertThat(stat.get("username")).isEqualTo("bob");
                    assertThat(stat.get("message_count")).isEqualTo(1);
                });
    }
}
