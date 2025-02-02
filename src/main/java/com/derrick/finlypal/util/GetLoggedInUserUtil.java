package com.derrick.finlypal.util;

import com.derrick.finlypal.entity.User;
import com.derrick.finlypal.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class GetLoggedInUserUtil {
    private static GetLoggedInUserUtil instance;
    private final UserRepository userRepository;

    public static User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null; // No authenticated user
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return instance.userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        } else if (principal instanceof String username) {
            return instance.userRepository.findByEmail(username).orElse(null);
        }
        return null;
    }

    @PostConstruct
    private void init() {
        instance = this;
    }
}
