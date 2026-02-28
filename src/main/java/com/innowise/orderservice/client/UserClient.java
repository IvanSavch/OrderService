package com.innowise.orderservice.client;

import com.innowise.orderservice.exception.ServiceUnavailableException;
import com.innowise.orderservice.exception.UserNotFoundException;
import com.innowise.orderservice.model.dto.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        HttpEntity<Long> httpEntity = setHeader();
        return restTemplate.exchange(url + "/users/email/{email}",HttpMethod.GET,httpEntity, UserDto.class, email).getBody();
    }

    public UserDto findByEmailFallback(String email, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + email + " not found");
        }
        throw new ServiceUnavailableException();
    }

    @CircuitBreaker(name = "userService", fallbackMethod = "findByIdFallback")
    public UserDto findById(Long id) {
        HttpEntity<Long> httpEntity = setHeader();
        return restTemplate.exchange(url + "/users/{id}", HttpMethod.GET, httpEntity, UserDto.class, id).getBody();
    }

    public UserDto findByIdFallback(Long id, Throwable throwable) throws ServiceUnavailableException {
        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("User with " + id + " not found");
        }
        throw new ServiceUnavailableException();
    }

    private HttpEntity<Long> setHeader(){
        HttpHeaders headers = new HttpHeaders();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long principal = (Long) authentication.getPrincipal();

        headers.set("UserId", principal.toString());
        headers.set("UserRoles", "ROLE_ADMIN");
        return new HttpEntity<>(headers);
    }
}
