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
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    void findByEmail() {
        String email = "test@mail.com";
        UserDto userDto = new UserDto();
        userDto.setEmail(email);

        when(restTemplate.getForEntity(baseUrl + "/users/email/{email}", UserDto.class, email))
                .thenReturn(ResponseEntity.ok(userDto));

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

        when(restTemplate.getForEntity(baseUrl + "/users/{id}", UserDto.class, 1L))
                .thenReturn(ResponseEntity.ok(userDto));

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