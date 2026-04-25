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
                taskEntity.getName(),
                taskEntity.getAssignedUserId(),
                taskEntity.getStatus(),
                taskEntity.getStartDateTime(),
                taskEntity.getDeadlineDate(),
                taskEntity.getPriority()
        );
    }

    public TaskEntity toEntity(
            Task task
    ){
        return new TaskEntity(
                task.id(),
                task.name(),
                task.assignedUserId(),
                task.status(),
                task.startDateTime(),
                task.deadlineDate(),
                task.priority()
        );
    }


}
