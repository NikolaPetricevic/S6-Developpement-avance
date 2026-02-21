package com.todolist.todolist.features.annonces.repository;

import com.todolist.todolist.features.annonces.entity.AnnonceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnonceRepository extends JpaRepository<AnnonceEntity, Long> {

}
