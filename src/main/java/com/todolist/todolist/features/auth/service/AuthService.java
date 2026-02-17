package com.todolist.todolist.features.auth.service;

import com.todolist.todolist.features.auth.dto.LoginDTO;
import com.todolist.todolist.features.users.entity.User;
import com.todolist.todolist.features.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class AuthService {

    private final UserRepository userRepository;

    private static final Map<String, Long> tokenMap = new ConcurrentHashMap<>();

    private static final Map<String, LocalDateTime> tokenExpiration = new ConcurrentHashMap<>();

    private static final int TOKEN_VALIDITY_HOURS = 1;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public LoginDTO login(String username, String password) {
        User user = userRepository.findByUsername(username);

        if (user != null && user.getPassword().equals(password)) {
            String token = generateToken();

            tokenMap.put(token, user.getId());
            tokenExpiration.put(token, LocalDateTime.now().plusHours(TOKEN_VALIDITY_HOURS));

            log.warn("User found, id : {}, name : {}", user.getId(), user.getUsername());

            return LoginDTO.builder()
                    .token(token)
                    .id(user.getId())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .build();
        }

        log.warn("User not found");

        return null;
    }

    public User getCurrentUser(String token) {
        if (!isValidToken(token)) {
            return null;
        }

        Long userId = tokenMap.get(token);
        if (userId != null) {
            return userRepository.findOne(userId);
        }

        return null;
    }

    public boolean isValidToken(String token) {
        if (token == null || !tokenMap.containsKey(token)) {
            return false;
        }

        LocalDateTime expiration = tokenExpiration.get(token);
        if (expiration == null || LocalDateTime.now().isAfter(expiration)) {
            tokenMap.remove(token);
            tokenExpiration.remove(token);
            return false;
        }

        return true;
    }

    private String generateToken() {
        return UUID.randomUUID().toString() + "-" + System.currentTimeMillis();
    }

    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

}
