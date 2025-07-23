package com.chatapp.restapi.security;


import com.chatapp.restapi.entity.CustomUserDetails;
import com.chatapp.restapi.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import static org.assertj.core.api.Assertions.assertThat;

class CustomUserDetailsTest {

    @Test
    void shouldReturnCorrectUserProperties() {
        User user = User.builder()
                .username("testuser")
                .password("secret")
                .role("ROLE_USER")
                .build();

        CustomUserDetails details = new CustomUserDetails(user);

        assertThat(details.getUsername()).isEqualTo("testuser");
        assertThat(details.getPassword()).isEqualTo("secret");
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
        assertThat(details.isAccountNonExpired()).isTrue();
        assertThat(details.isAccountNonLocked()).isTrue();
        assertThat(details.isCredentialsNonExpired()).isTrue();
        assertThat(details.isEnabled()).isTrue();
    }
}
