package com.example.project.utilities;

import com.example.project.models.Priority;
import com.example.project.models.Status;
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
        AND (:creatureId IS NULL OR t.creatureId = :creatureId)
        AND (:status IS NULL OR t.status = :status)
        AND (:priority IS NULL OR t.priority = :priority)
        """)
    List<TaskEntity> findTasksByOptionalParams(@Param("assignedUserId") Long assignedUserId,
                                               @Param("creatureId") Long creatureId,
                                               @Param("status") Status status,
                                               @Param("priority") Priority priority,
                                               Pageable pageable);
}