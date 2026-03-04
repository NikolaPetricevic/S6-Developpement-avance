package com.todolist.todolist.features.annonces.dto;

import com.todolist.todolist.features.annonces.enums.StatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnnonceFilterDTO {

    @Schema(example = "annonce")
    private String keyword;

    @Schema(example = "PUBLISHED")
    private StatusEnum status;

    @Schema(example = "1")
    private Long categoryId;

    @Schema(example = "1")
    private Long authorId;

    private LocalDate fromDate;
    private LocalDate toDate;
}
