package com.todolist.todolist.users.service;

import com.todolist.todolist.users.entity.User;
import com.todolist.todolist.users.repository.UserRepository;

public class UserService {

    private final UserRepository userRepository;

    public UserService() {
        this.userRepository = new UserRepository();
    }

    public User findOne(Long id) {
        return this.userRepository.findOne(id);
    }

}
