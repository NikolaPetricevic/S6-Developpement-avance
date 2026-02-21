package com.todolist.todolist.features.annonces.utils;

import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import com.todolist.todolist.features.annonces.enums.StatusEnum;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;

public class AnnonceSpecifications {

    public static Specification<AnnonceEntity> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            String pattern = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("title")), pattern),
                    cb.like(cb.lower(root.get("description")), pattern)
            );
        };
    }

    public static Specification<AnnonceEntity> hasStatus(StatusEnum status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<AnnonceEntity> hasCategoryId(Long categoryId) {
        return (root, query, cb) -> cb.equal(root.get("categoryEntity").get("id"), categoryId);
    }

    public static Specification<AnnonceEntity> hasAuthorId(Long authorId) {
        return (root, query, cb) -> cb.equal(root.get("author").get("id"), authorId);
    }

    public static Specification<AnnonceEntity> fromDate(LocalDate fromDate) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(
                root.get("date"),
                Timestamp.valueOf(fromDate.atStartOfDay())
        );
    }

    public static Specification<AnnonceEntity> toDate(LocalDate toDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(
                root.get("date"),
                Timestamp.valueOf(toDate.atTime(LocalTime.MAX))
        );
    }
}
