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
    @Column(name = "creature_id")
    Long creatureId;
    @Column(name = "doneDateTime")
    LocalDateTime doneDateTime;
    @Column(name = "assigned_user_id")
    Long assignedUserId;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    Status status;
    @Column(name = "create_data_time")
    LocalDateTime createDateTime;
    @Column(name = "deadline_data")
    LocalDateTime deadlineDate;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    Priority priority;


    public TaskEntity(){}

    public TaskEntity(Long id, Long creatureId, Long assignedUserId, Status status, LocalDateTime createDateTime, LocalDateTime deadlineDate, Priority priority){
        this.id = id;
        this.creatureId = creatureId;
        this.status = status;
        this.createDateTime = createDateTime;
        this.deadlineDate = deadlineDate;
        this.priority = priority;
        this.assignedUserId = assignedUserId;
    }

    public void setCreatureId(Long id) {
        this.creatureId = id;
    }

    public LocalDateTime getDoneDateTime() {
        return doneDateTime;
    }

    public void setDoneDateTime(LocalDateTime doneDateTime) {
        this.doneDateTime = doneDateTime;
    }

    public Long getCreatureId() {
        return creatureId;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public void setCreateDateTime(LocalDateTime createDateTime) {
        this.createDateTime = createDateTime;
    }

    public Long getAssignedUserId() {
        return assignedUserId;
    }

    public void setAssignedUserId(Long assignedUserId) {
        this.assignedUserId = assignedUserId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(LocalDateTime deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }
}
