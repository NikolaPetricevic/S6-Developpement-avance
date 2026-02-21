package com.todolist.todolist.features.auth.dto;

import com.todolist.todolist.features.users.enums.RoleEnum;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

    private String token;
    private Long userId;
    private String username;
    private RoleEnum role;

}
