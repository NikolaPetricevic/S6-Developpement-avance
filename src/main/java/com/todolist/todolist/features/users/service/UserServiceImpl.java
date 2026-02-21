package com.todolist.todolist.features.users.service;

import com.todolist.todolist.features.users.dto.UserDTO;
import com.todolist.todolist.features.users.exceptions.UserNotFoundException;
import com.todolist.todolist.features.users.mappers.UserMapper;
import com.todolist.todolist.features.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDTO> findAll() {
        return this.userRepository.findAll().stream().map(
                user -> {
                    return userMapper.toDTO(user);
                }
        ).toList();
    }

    @Override
    public UserDTO findOne(Long id) {
        return this.userRepository.findById(id).map(
                user -> {
                    return userMapper.toDTO(user);
                }
        ).orElseThrow(UserNotFoundException::new);
    }
}
