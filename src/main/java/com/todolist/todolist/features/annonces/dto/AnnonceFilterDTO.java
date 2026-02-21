package com.todolist.todolist.features.annonces.dto;

import com.todolist.todolist.features.annonces.enums.StatusEnum;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AnnonceFilterDTO {
    private String keyword;
    private StatusEnum status;
    private Long categoryId;
    private Long authorId;
    private LocalDate fromDate;
    private LocalDate toDate;
}
