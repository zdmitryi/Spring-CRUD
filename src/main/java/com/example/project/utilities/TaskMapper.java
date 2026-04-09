package com.example.project.utilities;

import com.example.project.models.Task;
import com.example.project.models.TaskEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskMapper {
    public Task toDomainTask(
            TaskEntity taskEntity
    ){
        return new Task(
                taskEntity.getId(),
                taskEntity.getCreatureId(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getCreateDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority(),
                LocalDateTime.now()
        );
    }

    public TaskEntity toEntity(
            Task task
    ){
        return new TaskEntity(
                task.id(),
                task.creatureId(),
                task.assignedUserId(),
                task.status(),
                task.createDateTime(),
                task.deadlineDate(),
                task.priority()
        );
    }


}
