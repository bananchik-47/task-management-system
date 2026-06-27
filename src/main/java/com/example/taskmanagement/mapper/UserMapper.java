package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.UserDto;
import com.example.taskmanagement.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toDto(User user);
}
