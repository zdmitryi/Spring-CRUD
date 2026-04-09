package com.example.project.utilities;

import com.example.project.models.Status;
import com.example.project.models.Task;
import com.example.project.models.TaskEntity;
import com.example.project.models.TaskSearchFilter;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
@Service
public class TaskService {
    private static final Logger log = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    @Autowired
    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper){
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }
    public Task getTaskById(long id){
        return taskMapper.toDomainTask(taskRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Не существует task с таким id")));
    }
    public List<Task> getAllFilterTasks(TaskSearchFilter filter){
        int pageSize = filter.pageSize() == null? 10 : filter.pageSize();
        int pageNumber = filter.pageNum() == null? 0 : filter.pageNum();
        var pageable = Pageable
                .ofSize(pageSize)
                .withPage(pageNumber);
        List<TaskEntity> listOfTaskEntity = taskRepository.findTasksByOptionalParams(
                filter.assignedUserId(),
                filter.creatureId(),
                filter.status(),
                filter.priority(),
                pageable
        );
        return listOfTaskEntity.stream().map(taskMapper::toDomainTask).toList();
    }

    public Task createTask(
            @Valid
            Task taskToCreate
    ) throws IllegalArgumentException{
        if (!isValidTask(taskToCreate)) throw new IllegalArgumentException("Дедлайн у Task раньше даты создания");
        if (taskToCreate.id() != null){
            throw new IllegalArgumentException("ID should be empty");
        }
        if (taskToCreate.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }
        TaskEntity taskToSave = taskMapper.toEntity(taskToCreate);
        taskToSave.setStatus(Status.CREATED);
        var saved = taskRepository.save(taskToSave);
        return taskMapper.toDomainTask(saved);
    }

    public Task putTaskById(long id, @Valid Task taskToPut){

        if (!isValidTask(taskToPut)) throw new IllegalArgumentException("Дедлайн у Task раньше даты создания");
        var updatedTask = taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Не существует Task с данным id"));

        if (taskToPut.id() != null){
            throw new IllegalArgumentException("ID should be empty");
        }
        if (taskToPut.status() != null){
            throw new IllegalArgumentException("Status should be empty");
        }
        if (updatedTask.getStatus() == Status.DONE){
            throw new IllegalArgumentException("Task is already done");
        }
        TaskEntity newTaskEntity = taskMapper.toEntity(taskToPut);
        newTaskEntity.setStatus(Status.REPLACED);
        var newTask = taskRepository.save(newTaskEntity);
        return taskMapper.toDomainTask(newTask);
    }

    public Task deleteTaskById(long id) {
        if (!taskRepository.existsById(id)) throw new NoSuchElementException("Не существует элемента с таким id");
        taskRepository.deleteById(id);
        return null;
    }

    public Task startTask(long id) throws RuntimeException{
            Task taskToMakeProgress = taskMapper.toDomainTask(taskRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Не существует Task с данным id")));
            if (taskToMakeProgress.status() == Status.IN_PROGRESS) return null;
            else if (taskToMakeProgress.assignedUserId() == null) {
                throw new IllegalStateException("Assigned id не должно быть пустым");
            }
            if (taskRepository.findTasksByOptionalParams(taskToMakeProgress.assignedUserId(), null, Status.IN_PROGRESS, null, null).size() > 4) throw new IllegalStateException("Превышено значение выполняемых Task для данного пользователя");
            else {
                TaskEntity newTaskEntity = taskMapper.toEntity(taskToMakeProgress);
                newTaskEntity.setStatus(Status.IN_PROGRESS);
                return taskMapper.toDomainTask(taskRepository.save(newTaskEntity));
            }
    }

    public Task completeTask(long id) {
        var taskToComplete = taskRepository.findById(id).orElseThrow(() -> new NoSuchElementException("Не существует элемента с таким id"));
        if (taskToComplete.getAssignedUserId() == null) throw new IllegalArgumentException("Assigned User Id не должно быть null");
        if (taskToComplete.getDeadlineDate() == null) throw new IllegalArgumentException("Deadline data не должна быть null");
        taskToComplete.setStatus(Status.DONE);
        taskToComplete.setDoneDateTime(LocalDateTime.now());
        return taskMapper.toDomainTask(taskRepository.save(taskToComplete));
    }

    public boolean isValidTask(Task task){
        return task.createDateTime().isBefore(task.deadlineDate());
    }
}
