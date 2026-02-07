package com.todolist.todolist.annonces.utils;

import com.todolist.todolist.annonces.enums.StatusEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnonceSearchCriteria {
    private String keyword;
    private StatusEnum status;
    private Long categoryId;
    private Long authorId;
    // Vous pouvez ajouter d'autres critères facilement

    public boolean hasKeyword() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    public boolean hasStatus() {
        return status != null;
    }

    public boolean hasCategoryId() {
        return categoryId != null;
    }

    public boolean hasAuthorId() {
        return authorId != null;
    }

    public boolean hasAnyCriteria() {
        return hasKeyword() || hasStatus() || hasCategoryId() || hasAuthorId();
    }
}
