package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;

@Service
public class UserClient {
    private final RestTemplate restTemplate;

    public UserClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "userServiceFallback")
    public UserDto findByEmail(String email) {
            return restTemplate.getForEntity("http://localhost:8081/users/email/{email}", UserDto.class, email).getBody();
    }
    public UserDto userServiceFallback(String email, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + email +" not found");
        }
        throw new ServiceUnavailableException("User service is unavailable");
    }
    @CircuitBreaker(name = "userService", fallbackMethod = "userServiceFallback")
    public UserDto findById(Long id) {
        return restTemplate.getForEntity("http://localhost:8081/users/{id}", UserDto.class, id).getBody();
    }
    public UserDto userServiceFallback(Long id, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + id +" not found");
        }
        throw new ServiceUnavailableException("User service is unavailable");
    }
}
