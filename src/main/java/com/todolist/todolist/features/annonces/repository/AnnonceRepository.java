package com.todolist.todolist.features.annonces.repository;

import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AnnonceRepository extends JpaRepository<AnnonceEntity, Long>, JpaSpecificationExecutor<AnnonceEntity> {

    @EntityGraph(attributePaths = {"author", "categoryEntity"})
    Page<AnnonceEntity> findAll(Specification<AnnonceEntity> spec, Pageable pageable);
}
