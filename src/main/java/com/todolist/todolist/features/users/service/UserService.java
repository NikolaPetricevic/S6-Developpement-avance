package com.todolist.todolist.features.users.service;

import com.todolist.todolist.features.users.repository.UserRepository;
import com.todolist.todolist.features.users.entity.User;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User findOne(Long id) {
        return this.userRepository.findOne(id);
    }

}
