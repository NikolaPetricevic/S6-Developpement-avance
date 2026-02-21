package com.todolist.todolist.features.auth.controller;

import com.todolist.todolist.features.auth.dto.LoginRequestDTO;
import com.todolist.todolist.features.auth.dto.LoginResponseDTO;
import com.todolist.todolist.features.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentification")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Se connecter et récupérer un token JWT")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        return ResponseEntity.ok(authService.login(loginRequestDTO));
    }
}
