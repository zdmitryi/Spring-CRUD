package com.example.project.models;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
public record Task(
        Long id,
        @NotNull
        String name,
        @NotNull
        Long assignedUserId,
        Status status,
        @NotNull
        LocalDateTime startDateTime,
        @NotNull
        @Future
        LocalDateTime deadlineDate,
        @NotNull
        Priority priority
) {
    public enum Status {
        CREATED,
        IN_PROGRESS,
        DONE,
        REPLACED
    }

    public enum Priority {
        LOW,
        MEDIUM,
        HIGH
    }
}
