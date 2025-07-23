package com.chatapp.restapi.controller;

import com.chatapp.restapi.dto.UserDTO;
import com.chatapp.restapi.entity.User;
import com.chatapp.restapi.repository.UserRepository;
import com.chatapp.restapi.service.AuthService;
import com.chatapp.restapi.service.MessageService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;

    // 1. Register a new user
    @PostMapping("/users")
    public ResponseEntity<?> registerUser(@RequestBody Map<String, String> req) {
        String username = req.get("username");
        String password = req.get("password");
        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Username and password required");
        }
        try {
            authService.registerUser(username, password);
            return ResponseEntity.ok("User registered");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 2. Delete a user and anonymize their messages
    @DeleteMapping("/users/{username}")
    @Transactional
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = userOpt.get();
        // Find or create anonymous user
        User anonymous = userRepository.findByUsername("anonymous")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("anonymous")
                        .password("")
                        .role("ROLE_USER")
                        .build()));
        // Anonymize messages
        messageService.anonymizeMessages(user, anonymous);
        userRepository.delete(user);
        return ResponseEntity.ok("User deleted and messages anonymized");
    }

    // 3. Get statistics
    @GetMapping("/statistics")
    public ResponseEntity<List<Map<String, Object>>> getStatistics() {
        List<Map<String, Object>> stats = messageService.getUserStatistics();
        return ResponseEntity.ok(stats);
    }
} 