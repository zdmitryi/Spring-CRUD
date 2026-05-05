package com.example.project;

import com.example.project.models.Task;
import com.example.project.models.TaskEntity;
import com.example.project.models.User;
import com.example.project.models.WebUser;
import com.example.project.utilities.TaskMapper;
import com.example.project.utilities.TaskRepository;
import com.example.project.utilities.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class TaskServiceTests {

    private Task task;
    private WebUser webUser;

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskMapper taskMapper = new TaskMapper();

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    void setUp() {
        this.task = new Task(
                null, "test", null, null,
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(2),
                Task.Priority.MEDIUM
        );

        this.webUser = new WebUser(
                "testName", "12345",
                new HashSet<>(Set.of(WebUser.Role.DEFAULT_USER))
        );
    }

    @Test
    @DisplayName("Creating test")
    void creatingTest() {
        when(taskRepository.save(any(TaskEntity.class)))
                .thenAnswer((Answer<TaskEntity>) invocation -> {  // ← Явный каст
                    TaskEntity te = invocation.getArgument(0);
                    te.setId(1L);
                    te.setAssignedUserId(1L);
                    te.setStatus(Task.Status.CREATED);
                    return te;
                });

        Task result = taskService.createTask(task, webUser);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("test", result.name()),
                () -> assertEquals(Task.Status.CREATED, result.status()),
                () -> assertEquals(1L, result.assignedUserId()),
                () -> assertEquals(Task.Priority.MEDIUM, result.priority())
        );

        verify(taskRepository).save(any(TaskEntity.class));
    }

    @Test
    @DisplayName("Starting task test")
    void startingTest() {
        TaskEntity existingEntity = new TaskEntity();
        existingEntity.setId(1L);
        existingEntity.setName("test");
        existingEntity.setAssignedUserId(1L);
        existingEntity.setStatus(Task.Status.CREATED);

        TaskEntity updatedEntity = new TaskEntity();
        updatedEntity.setId(1L);
        updatedEntity.setName("test");
        updatedEntity.setAssignedUserId(1L);
        updatedEntity.setStatus(Task.Status.IN_PROGRESS);

        Task existingTask = new Task(1L, "test", 1L, Task.Status.CREATED,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), Task.Priority.MEDIUM);

        Task expectedTask = new Task(1L, "test", 1L, Task.Status.IN_PROGRESS,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), Task.Priority.MEDIUM);

        WebUser user = new WebUser( "testName", "12345", Set.of(WebUser.Role.DEFAULT_USER));
        user.setId(1L);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingEntity));

        when(taskRepository.findTasksByOptionalParams(
                eq("test"), eq(1L), eq(Task.Status.IN_PROGRESS), isNull(), isNull()))
                .thenReturn(List.of());

        when(taskMapper.toDomainTask(existingEntity))
                .thenReturn(existingTask);

        when(taskMapper.toEntity(existingTask))
                .thenReturn(updatedEntity);

        when(taskRepository.save(updatedEntity))
                .thenReturn(updatedEntity);

        when(taskMapper.toDomainTask(updatedEntity))
                .thenReturn(expectedTask);

        Task result = taskService.startTask(1L, user);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("test", result.name()),
                () -> assertEquals(Task.Status.IN_PROGRESS, result.status()),
                () -> assertEquals(1L, result.assignedUserId())
        );

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(updatedEntity);
    }


    @Test
    @DisplayName("Completing task test")
    void completionTest() {
        TaskEntity existingEntity = new TaskEntity();
        existingEntity.setId(1L);
        existingEntity.setName("test");
        existingEntity.setAssignedUserId(1L);
        existingEntity.setStatus(Task.Status.IN_PROGRESS);
        existingEntity.setDeadlineDate(LocalDateTime.now().plusDays(2));

        TaskEntity completedEntity = new TaskEntity();
        completedEntity.setId(1L);
        completedEntity.setName("test");
        completedEntity.setAssignedUserId(1L);
        completedEntity.setStatus(Task.Status.DONE);
        completedEntity.setDeadlineDate(LocalDateTime.now().plusDays(2));
        completedEntity.setDoneDateTime(LocalDateTime.now());

        Task expectedTask = new Task(1L, "test", 1L, Task.Status.DONE,
                LocalDateTime.now(), LocalDateTime.now().plusDays(2), Task.Priority.MEDIUM);

        WebUser user = new WebUser("testName", "12345", Set.of(WebUser.Role.DEFAULT_USER));
        user.setId(1L);

        when(taskRepository.findById(1L))
                .thenReturn(Optional.of(existingEntity));

        when(taskRepository.save(existingEntity))
                .thenReturn(completedEntity);

        when(taskMapper.toDomainTask(completedEntity))
                .thenReturn(expectedTask);

        Task result = taskService.completeTask(1L, user);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1L, result.id()),
                () -> assertEquals("test", result.name()),
                () -> assertEquals(Task.Status.DONE, result.status()),
                () -> assertEquals(1L, result.assignedUserId())
        );

        verify(taskRepository).findById(1L);
        verify(taskRepository).save(existingEntity);
        verify(taskMapper).toDomainTask(completedEntity);
    }




}

