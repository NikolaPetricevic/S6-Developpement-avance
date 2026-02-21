package com.todolist.todolist.features.auth.service;

import com.todolist.todolist.features.auth.dto.LoginRequestDTO;
import com.todolist.todolist.features.auth.dto.LoginResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO loginRequestDTO);

}
