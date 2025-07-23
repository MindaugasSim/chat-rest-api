package com.chatapp.restapi.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserDTOTest {

    @Test
    void shouldMatchAllUserDtoFields() {
        UserDTO actual = UserDTO.builder()
                .username("testuser")
                .role("USER")
                .build();

        UserDTO expected = new UserDTO();
        expected.setUsername("testuser");
        expected.setRole("USER");

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
