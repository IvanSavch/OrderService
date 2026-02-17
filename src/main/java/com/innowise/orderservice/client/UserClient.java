package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;

@Component
public class UserClient {
    private final RestTemplate restTemplate;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "findByEmailFallback")
    public UserDto findByEmail(String email) {
            return restTemplate.getForEntity("http://localhost:8081/users/email/{email}", UserDto.class, email).getBody();
    }
    public UserDto findByEmailFallback(String email, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + email +" not found");
        }
        throw new ServiceUnavailableException("User service is unavailable");
    }
    @CircuitBreaker(name = "userService", fallbackMethod = "findByIdFallback")
    public UserDto findById(Long id) {
        return restTemplate.getForEntity("http://localhost:8081/users/{id}", UserDto.class, id).getBody();
    }
    public UserDto findByIdFallback(Long id, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + id +" not found");
        }
        throw new ServiceUnavailableException("User service is unavailable");
    }
}
