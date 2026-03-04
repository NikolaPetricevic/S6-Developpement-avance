package com.todolist.todolist.features.auth.service;

import com.todolist.todolist.features.auth.controller.descriptions.LoginApiDoc;
import com.todolist.todolist.features.auth.dto.LoginRequestDTO;
import com.todolist.todolist.features.auth.dto.LoginResponseDTO;
import com.todolist.todolist.features.auth.exceptions.InvalidCredentialsException;
import com.todolist.todolist.features.users.entity.UserEntity;
import com.todolist.todolist.features.users.repository.UserRepository;
import com.todolist.todolist.features.users.exceptions.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
        UserEntity user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(UserNotFoundException::new);

        if (!loginRequestDTO.getPassword().equals(user.getPassword())) {
            throw new InvalidCredentialsException();
        }

        return LoginResponseDTO.builder()
                .token(jwtService.generateToken(user))
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
    }
}