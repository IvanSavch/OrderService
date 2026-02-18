package com.innowise.orderservice.service;

import org.springframework.security.core.Authentication;

public interface AuthenticationService {
   boolean adminRole(Authentication authentication);
   boolean isSelf(Long userId, Authentication authentication);
}
