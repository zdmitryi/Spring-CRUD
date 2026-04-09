package com.example.project.controller;

import com.example.project.models.Priority;
import com.example.project.models.Task;
import com.example.project.models.TaskSearchFilter;
import com.example.project.utilities.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {
    private static final Logger log = LoggerFactory.getLogger(TaskController.class);
    TaskService taskService;
    @Autowired
    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaskById(

            @PathVariable("id") long id
    ){
            log.info("Called TaskId");
            return ResponseEntity.status(HttpStatus.OK).body(taskService.getTaskById(id));
    }

    @GetMapping()
    public ResponseEntity<List<Task>> getAllFilterTasks(
            @RequestParam(name = "creatureId", required = false) Long creatureId,
            @RequestParam(name = "assignedUserId", required = false) Long assignedUserId,
            @RequestParam(name = "priority", required = false) Priority priority,
            @RequestParam(name = "pageSize", required = false) Integer pageSize,
            @RequestParam(name = "pageNumber", required = false) Integer pageNumber
    ){
        log.info("Called getAllFilerTasks");
        var filter = new TaskSearchFilter(
                creatureId,
                assignedUserId,
                null,
                priority,
                pageSize,
                pageNumber
        );
        return ResponseEntity.status(HttpStatus.OK).body(taskService.getAllFilterTasks(filter));
    }
    @PostMapping()
    public ResponseEntity<Task> createTask(
            @RequestBody
            Task taskToCreate
    ){
        log.info("New Task Created");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(taskToCreate));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(
            @PathVariable("id") long id
    ){
        log.info("Called complete task");
        return ResponseEntity.status(HttpStatus.OK).body(taskService.completeTask(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> putTaskById(
            @RequestBody
            Task taskToPut,
            @PathVariable("id") long id

    ){
        log.info("Called PutId");
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.putTaskById(id, taskToPut));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaskById(

            @PathVariable("id") long id
    ) {
        log.info("Called DeleteId");
        return ResponseEntity.status(HttpStatus.OK).body(taskService.deleteTaskById(id));
    }
    @PutMapping("/{id}/start")
    public ResponseEntity<?> makeTaskProgress(
            @PathVariable("id") long id
    ) {
            log.info("Called makeTaskProgress, id: " + id);
            return ResponseEntity.status(HttpStatus.OK).body(taskService.startTask(id));
        }
}
