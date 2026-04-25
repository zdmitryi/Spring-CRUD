package com.example.project.utilities;

import com.example.project.models.Task;
import com.example.project.models.TaskEntity;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    @Query("""
        SELECT t FROM TaskEntity t 
        WHERE (:assignedUserId IS NULL OR t.assignedUserId = :assignedUserId)
        AND (:name IS NULL OR t.name = :name)
        AND (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        """)
    List<TaskEntity> findTasksByOptionalParams(
            @Param("name") String name,
            @Param("assignedUserId") Long assignedUserId,
            @Param("status") Task.Status status,
            @Param("priority") Task.Priority priority,
            Pageable pageable);
    record TaskSearchFilter(
            String name,
            Long assignedUserId,
            Task.Status status,
            Task.Priority priority,
            Integer pageSize,
            Integer pageNum
    ) {}
}