package com.example.project.models;

public record TaskSearchFilter(
        Long creatureId,
        Long assignedUserId,
        Status status,
        Priority priority,
        Integer pageSize,
        Integer pageNum
) {}
