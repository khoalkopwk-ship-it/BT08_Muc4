package vn.iotstar.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.iotstar.entity.User;
import vn.iotstar.repository.UserRepository;

@ControllerAdvice
@RequiredArgsConstructor

// Cung cấp thông tin người dùng hiện tại cho tất cả các controller
public class GlobalModelAdvice {
    private final UserRepository userRepository;

    @ModelAttribute("currentUser")
    public User currentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) return null;
        return userRepository.findByUsernameIgnoreCase(authentication.getName()).orElse(null);
    }
}