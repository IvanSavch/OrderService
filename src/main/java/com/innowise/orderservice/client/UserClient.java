package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.ServiceUnavailableException;
import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class UserClient {
    private final RestTemplate restTemplate;
    @Value("${user.service.url}")
    private String url;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "findByEmailFallback")
    public UserDto findByEmail(String email) {
        return restTemplate.getForEntity(url + "/users/email/{email}", UserDto.class, email).getBody();
    }

    public UserDto findByEmailFallback(String email, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + email + " not found");
        }
        throw new ServiceUnavailableException();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "findByIdFallback")
    public UserDto findById(Long id) {
        return restTemplate.getForEntity(url + "/users/{id}", UserDto.class, id).getBody();
    }

    public UserDto findByIdFallback(Long id, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + id + " not found");
        }
        throw new ServiceUnavailableException();
    }
}
