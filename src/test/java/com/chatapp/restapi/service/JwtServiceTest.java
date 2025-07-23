package com.chatapp.restapi.service;

import com.chatapp.restapi.entity.User;
import com.chatapp.restapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtServiceTest {

    private UserRepository userRepository;
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        jwtService = new JwtService(userRepository);
    }

    @Test
    void shouldLoadUserByUsername() {
        User mockUser = User.builder()
                .username("testuser")
                .password("secret")
                .role("ROLE_USER")
                .build();

        Mockito.when(userRepository.findByUsername("testuser"))
                .thenReturn(Optional.of(mockUser));

        UserDetails result = jwtService.loadUserByUsername("testuser");

        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getAuthorities()).extracting("authority").contains("ROLE_USER");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        Mockito.when(userRepository.findByUsername("ghost"))
                .thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> jwtService.loadUserByUsername("ghost"));
    }
}
