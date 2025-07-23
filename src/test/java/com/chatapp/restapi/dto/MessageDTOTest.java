package com.chatapp.restapi.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MessageDTOTest {

    @Test
    void shouldMatchAllMessageDtoFields() {
        MessageDTO actual = MessageDTO.builder()
                .content("hello")
                .username("testuser")
                .build();

        MessageDTO expected = new MessageDTO();
        expected.setContent("hello");
        expected.setUsername("testuser");

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }
}
