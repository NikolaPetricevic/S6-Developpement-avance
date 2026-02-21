package com.todolist.todolist.features.users.repository;

import com.todolist.todolist.features.users.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
