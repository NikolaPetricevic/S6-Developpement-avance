package com.todolist.todolist.features.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "username is required")
    @Schema(example = "john")
    private String username;

    @NotBlank(message = "password is required")
    @Schema(example = "MotDePasse1234")
    private String password;

}
