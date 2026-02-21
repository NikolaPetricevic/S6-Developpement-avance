package com.todolist.todolist.features.users.mappers;

import com.todolist.todolist.features.users.dto.UserDTO;
import com.todolist.todolist.features.users.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserDTO dto);

    UserDTO toDTO(UserEntity entity);
}
