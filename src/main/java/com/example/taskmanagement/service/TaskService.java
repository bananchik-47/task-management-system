package com.example.taskmanagement.service;

import com.example.taskmanagement.dto.TaskCreateRequest;
import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.TaskUpdateRequest;
import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.Task.Priority;
import com.example.taskmanagement.entity.Task.Status;
import com.example.taskmanagement.entity.User;
import com.example.taskmanagement.exception.ResourceNotFoundException;
import com.example.taskmanagement.mapper.TaskMapper;
import com.example.taskmanagement.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final UserService userService;

    @Transactional
    public TaskDto createTask(TaskCreateRequest request) {
        User currentUser = userService.getAuthenticatedUser();

        Task task = taskMapper.toEntity(request);
        task.setUser(currentUser);

        Task savedTask = taskRepository.save(task);
        return taskMapper.toDto(savedTask);
    }

    @Transactional
    public TaskDto updateTask(UUID id, TaskUpdateRequest request) {
        User currentUser = userService.getAuthenticatedUser();
        Task task = findTaskForUser(id, currentUser.getId());

        taskMapper.updateEntity(request, task);
        return taskMapper.toDto(taskRepository.save(task));
    }

    @Transactional
    public void deleteTask(UUID id) {
        User currentUser = userService.getAuthenticatedUser();
        Task task = findTaskForUser(id, currentUser.getId());
        taskRepository.delete(task);
    }

    @Transactional(readOnly = true)
    public TaskDto getTaskById(UUID id) {
        User currentUser = userService.getAuthenticatedUser();
        Task task = findTaskForUser(id, currentUser.getId());
        return taskMapper.toDto(task);
    }

    @Transactional(readOnly = true)
    public List<TaskDto> getAllTasks(Optional<Status> status, Optional<Priority> priority) {
        User currentUser = userService.getAuthenticatedUser();
        UUID userId = currentUser.getId();

        List<Task> tasks;
        if (status.isPresent() && priority.isPresent()) {
            tasks = taskRepository.findByUserIdAndStatusAndPriority(userId, status.get(), priority.get());
        } else if (status.isPresent()) {
            tasks = taskRepository.findByUserIdAndStatus(userId, status.get());
        } else if (priority.isPresent()) {
            tasks = taskRepository.findByUserIdAndPriority(userId, priority.get());
        } else {
            tasks = taskRepository.findAllByUserId(userId);
        }

        return tasks.stream().map(taskMapper::toDto).toList();
    }

    private Task findTaskForUser(UUID taskId, UUID userId) {
        return taskRepository.findByIdAndUserId(taskId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));
    }
}
