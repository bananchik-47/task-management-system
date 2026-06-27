package com.example.taskmanagement.repository;

import com.example.taskmanagement.entity.Task;
import com.example.taskmanagement.entity.Task.Priority;
import com.example.taskmanagement.entity.Task.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId")
    List<Task> findAllByUserId(@Param("userId") UUID userId);

    @Query("SELECT t FROM Task t WHERE t.id = :id AND t.user.id = :userId")
    Optional<Task> findByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = :status")
    List<Task> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") Status status);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.priority = :priority")
    List<Task> findByUserIdAndPriority(@Param("userId") UUID userId, @Param("priority") Priority priority);

    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.status = :status AND t.priority = :priority")
    List<Task> findByUserIdAndStatusAndPriority(
            @Param("userId") UUID userId,
            @Param("status") Status status,
            @Param("priority") Priority priority);
}
