package com.todolist.todolist.features.auth.dto;

import com.todolist.todolist.features.users.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponseDTO {

    @Schema(example = "token")
    private String token;

    @Schema(example = "1")
    private Long userId;

    @Schema(example = "john")
    private String username;

    @Schema(example = "ROLE_USER")
    private RoleEnum role;

}
