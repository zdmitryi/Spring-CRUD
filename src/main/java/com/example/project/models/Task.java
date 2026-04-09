package com.example.project.models;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
public record Task(
        Long id,
        @NotNull
        Long creatureId,

        Long assignedUserId,

        Status status,

        @NotNull
        LocalDateTime createDateTime,
        @NotNull
        @Future
        LocalDateTime deadlineDate,
        @NotNull
        Priority priority,
        LocalDateTime doneDateTime
) {

}
