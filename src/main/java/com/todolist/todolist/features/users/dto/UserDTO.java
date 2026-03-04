package com.todolist.todolist.features.users.dto;

import com.todolist.todolist.features.users.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY, example = "1")
    private Long id;

    @NotBlank(message = "username is required")
    @Schema(example = "john")
    private String username;

    @NotBlank(message = "email is required")
    @Schema(example = "john@mail.com")
    private String email;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY, example = "MotDePasse1234")
    @NotBlank(message = "password is required")
    private String password;

    @Schema(example = "ROLE_USER")
    private RoleEnum role;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDateTime createdAt;
}
