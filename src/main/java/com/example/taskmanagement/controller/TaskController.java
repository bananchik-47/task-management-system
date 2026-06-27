package com.example.taskmanagement.controller;

import com.example.taskmanagement.dto.TaskCreateRequest;
import com.example.taskmanagement.dto.TaskDto;
import com.example.taskmanagement.dto.TaskUpdateRequest;
import com.example.taskmanagement.entity.Task.Priority;
import com.example.taskmanagement.entity.Task.Status;
import com.example.taskmanagement.security.UserDetailsImpl;
import com.example.taskmanagement.service.TaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    public List<TaskDto> getAllTasks(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestParam Optional<Status> status,
            @RequestParam Optional<Priority> priority) {
        return taskService.getAllTasks(status, priority);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {
        return taskService.getTaskById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskDto createTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody TaskCreateRequest request) {
        return taskService.createTask(request);
    }

    @PutMapping("/{id}")
    public TaskDto updateTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id,
            @Valid @RequestBody TaskUpdateRequest request) {
        return taskService.updateTask(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable UUID id) {
        taskService.deleteTask(id);
    }
}
