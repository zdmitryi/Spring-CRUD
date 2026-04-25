package com.example.project.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tasks")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
    @Column(name = "name")
    String name;
    @Column(name = "doneDateTime")
    LocalDateTime doneDateTime;
    @Column(name = "assigned_user_id")
    Long assignedUserId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Task.Status status;
    @Column(name = "start_data_time")
    LocalDateTime startDateTime;
    @Column(name = "deadline_data")
    LocalDateTime deadlineDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    Task.Priority priority;


    public TaskEntity(){}

    public TaskEntity(Long id, String name, Long assignedUserId, Task.Status status, LocalDateTime startDateTime, LocalDateTime deadlineDate, Task.Priority priority){
        this.id = id;
        this.name = name;
        this.status = status;
        this.startDateTime = startDateTime;
        this.deadlineDate = deadlineDate;
        this.priority = priority;
        this.assignedUserId = assignedUserId;
    }


    public LocalDateTime getDoneDateTime() {
        return doneDateTime;
    }

    public void setDoneDateTime(LocalDateTime doneDateTime) {
        this.doneDateTime = doneDateTime;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(LocalDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Task.Status getStatus() {
        return status;
    }

    public void setStatus(Task.Status status) {
        this.status = status;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Task.Priority getPriority() {
        return priority;
    }

    public void setPriority(Task.Priority priority) {
        this.priority = priority;
    }
}
