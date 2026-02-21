package com.todolist.todolist.features.users.service;

import com.todolist.todolist.features.users.dto.UserDTO;

import java.util.List;

public interface UserService {

    public List<UserDTO> findAll();

    public UserDTO findOne(Long id);
}
