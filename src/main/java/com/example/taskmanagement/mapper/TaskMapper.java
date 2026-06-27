package com.example.taskmanagement.mapper;

import com.example.taskmanagement.dto.TaskCreateRequest;
import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.TaskUpdateRequest;
import com.example.taskmanagement.entity.Task;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface TaskMapper {

    @Mapping(target = "userId", source = "user.id")
    TaskDto toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", defaultExpression = "java(com.example.taskmanagement.entity.Task.Status.NEW)")
    Task toEntity(TaskCreateRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(TaskUpdateRequest request, @MappingTarget Task task);
}
