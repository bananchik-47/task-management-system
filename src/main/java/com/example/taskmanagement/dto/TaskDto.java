package com.example.taskmanagement.dto;

import com.example.taskmanagement.entity.Task.Priority;
import com.example.taskmanagement.entity.Task.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {

    private UUID id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private UUID userId;
    private Instant createdAt;
    private Instant updatedAt;
}
