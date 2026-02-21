package com.todolist.todolist.features.annonces.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import com.todolist.todolist.features.categories.dto.CategoryDTO;
import com.todolist.todolist.features.users.dto.UserDTO;
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
@Data
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
    private UserDTO author;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "author_id is required")
    private Long author_id;

    @Schema(accessMode = Schema.AccessMode.READ_ONLY)
    private CategoryDTO category;

    @Schema(accessMode = Schema.AccessMode.WRITE_ONLY)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NotNull(message = "category_id is required")
    private Long category_id;

    @Override
    public String toString() {
        return "AnnonceDTO{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", adress='" + adress + '\'' +
                ", mail='" + mail + '\'' +
                ", date=" + date +
                ", status=" + status +
                ", author_id=" + author_id +
                ", category_id=" + category_id +
                '}';
    }
}
