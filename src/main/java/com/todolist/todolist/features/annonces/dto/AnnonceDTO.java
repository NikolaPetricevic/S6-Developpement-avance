package com.todolist.todolist.features.annonces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.categories.entity.Category;
import com.todolist.todolist.features.users.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.sql.Timestamp;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnonceDTO {

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "adress is required")
    private String adress;

    @NotBlank(message = "mail is required")
    private String mail;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Timestamp date;

    @NotNull(message = "status is required")
    private StatusEnum status;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private User author;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "author_id is required")
    private Long author_id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private Category category;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "category_id is required")
    private Long category_id;
}
