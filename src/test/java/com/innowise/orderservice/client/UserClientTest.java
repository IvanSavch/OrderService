package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.ServiceUnavailableException;
import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserClientTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private UserClient userClient;

    private final String baseUrl = "http://test";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(userClient, "url", baseUrl);
    }
    @BeforeEach
    void setUpAuthentication() {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
        Authentication authentication = new UsernamePasswordAuthenticationToken(1L, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void findByEmail() {
        String email = "test@mail.com";
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);
        when(restTemplate.exchange(eq(baseUrl + "/users/email/{email}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UserDto.class),
                eq(email))).thenReturn(responseEntity);

        UserDto result = userClient.findByEmail(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
    }

    @Test
    void findByEmailFallbackUserNotFoundException() {
        String email = "test@mail.com";

        HttpClientErrorException exception = HttpClientErrorException.create(org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null);

        assertThrows(UserNotFoundException.class, () -> userClient.findByEmailFallback(email, exception));
    }

    @Test
    void findByEmailFallbackServiceUnavailableException() {
        String email = "test@mail.com";

        RuntimeException exception = new RuntimeException("Server unavailable error");

        assertThrows(ServiceUnavailableException.class, () -> userClient.findByEmailFallback(email, exception));
    }

    @Test
    void findById() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);

        ResponseEntity<UserDto> responseEntity = ResponseEntity.ok(userDto);
        when(restTemplate.exchange(eq(baseUrl + "/users/{id}"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(UserDto.class),
                eq(1L))).thenReturn(responseEntity);

        UserDto result = userClient.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void findByIdFallbackUserNotFoundException() {
        HttpClientErrorException exception = HttpClientErrorException.create(org.springframework.http.HttpStatus.NOT_FOUND,
                        "Not Found",
                        null,
                        null,
                        null);

        assertThrows(UserNotFoundException.class,
                () -> userClient.findByIdFallback(1L, exception));
    }

    @Test
    void findByIdFallbackServiceUnavailableException() {
        RuntimeException exception = new RuntimeException("Server unavailable error");
        assertThrows(ServiceUnavailableException.class, () -> userClient.findByIdFallback(1L, exception));
    }
}