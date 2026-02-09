package com.innowise.orderservice.service.user;

import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.naming.ServiceUnavailableException;

@Service
public class UserService {
    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @CircuitBreaker(name = "userService", fallbackMethod = "userServiceFallback")
    public UserDto findByEmail(String email){
        return restTemplate.getForEntity("http://localhost:8080/users/email/{email}",UserDto.class,email).getBody();
    }
    public UserDto userServiceFallback(String email,Throwable throwable) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("User service is unavailable");
    }
}
