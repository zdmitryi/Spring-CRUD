package com.example.project;

import com.example.project.controller.TaskController;
import com.example.project.models.Task;
import com.example.project.models.User;
import com.example.project.models.WebUser;
import com.example.project.utilities.TaskMapper;
import com.example.project.utilities.TaskRepository;
import com.example.project.utilities.TaskService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;

import static com.example.project.models.WebUser.Role.DEFAULT_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
@ActiveProfiles("test")
@DisplayName("TaskController тесты")
public class TaskControllerTests {
    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    @Test
    @DisplayName("GET all tasks")
    void getAllTask() throws Exception {
        Task task1 = new Task(1L, "test1", 1L, Task.Status.CREATED, LocalDateTime.parse("2025-05-05T00:00:00"), LocalDateTime.parse("2025-05-06T00:00:00"), Task.Priority.MEDIUM);
        Task task2 = new Task(2L, "test2", 2L, Task.Status.CREATED, LocalDateTime.parse("2025-05-02T00:00:00"), LocalDateTime.parse("2025-05-07T00:00:00"), Task.Priority.LOW);


        when(taskService.getAllFilterTasks(any())).thenReturn(List.of(task1, task2));

        mockMvc.perform(get("/task"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("test1"))
                .andExpect(jsonPath("$[1].name").value("test2"));

        verify(taskService).getAllFilterTasks(any());
    }

    @Test
    @DisplayName("GET task by ID")
    void getTask() throws Exception {
        Task task1 = new Task(1L, "test1", 1L, Task.Status.CREATED, LocalDateTime.parse("2025-05-05T00:00:00"), LocalDateTime.parse("2025-05-06T00:00:00"), Task.Priority.MEDIUM);

        when(taskService.getTaskById(1L)).thenReturn(task1);
        when(taskService.getTaskById(2L)).thenThrow(new EntityNotFoundException("Нет task с таким ID"));

        mockMvc.perform(get("/task/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("test1"));

        mockMvc.perform(get("/task/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST task")
    void createTask() throws Exception {
        Task inputTask = new Task(
                null, "test1", null, null,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                LocalDateTime.parse("2025-05-06T00:00:00"),
                Task.Priority.MEDIUM
        );

        Task createdTask = new Task(
                1L, "test1", 1L, Task.Status.CREATED,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                LocalDateTime.parse("2025-05-06T00:00:00"),
                Task.Priority.MEDIUM
        );

        when(taskService.createTask(any(Task.class), any(User.class)))
                .thenReturn(createdTask);

        WebUser webUser = new WebUser("testUser", "password",
                Set.of(WebUser.Role.DEFAULT_USER));

        Authentication auth = new UsernamePasswordAuthenticationToken(
                webUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(post("/task")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("test1"));

        verify(taskService).createTask(any(Task.class), any(User.class));
    }

    @Test
    @DisplayName("PUT task")
    void putTask() throws Exception {
        Task inputTask = new Task(
                null, "updated", 1L, null,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                LocalDateTime.parse("2025-05-06T00:00:00"),
                Task.Priority.MEDIUM
        );

        Task updatedTask = new Task(
                1L, "updated", 1L, Task.Status.REPLACED,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                LocalDateTime.parse("2025-05-06T00:00:00"),
                Task.Priority.MEDIUM
        );

        when(taskService.putTaskById(eq(1L), any(Task.class), any(User.class)))
                .thenReturn(updatedTask);

        WebUser webUser = new WebUser("testUser", "password",
                Set.of(WebUser.Role.DEFAULT_USER));
        webUser.setId(1L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                webUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(put("/task/1")
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputTask)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("updated"))
                .andExpect(jsonPath("$.status").value("REPLACED"));

        verify(taskService).putTaskById(eq(1L), any(Task.class), any(User.class));
    }

    @Test
    @DisplayName("DELETE task")
    void deleteTask() throws Exception {
        when(taskService.deleteTaskById(eq(1L), any(WebUser.class)))
                .thenReturn(null);

        WebUser webUser = new WebUser("testUser", "password",
                Set.of(WebUser.Role.DEFAULT_USER));
        webUser.setId(1L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                webUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(delete("/task/1")
                        .with(authentication(auth)))
                .andExpect(status().isOk());

        verify(taskService).deleteTaskById(eq(1L), any(WebUser.class));
    }

    @Test
    @DisplayName("PUT task")
    void startTask() throws Exception {
        Task startedTask = new Task(
                1L, "test1", 1L, Task.Status.IN_PROGRESS,
                LocalDateTime.parse("2025-05-05T00:00:00"),
                LocalDateTime.parse("2025-05-06T00:00:00"),
                Task.Priority.MEDIUM
        );

        when(taskService.startTask(eq(1L), any(WebUser.class)))
                .thenReturn(startedTask);

        WebUser webUser = new WebUser("testUser", "password",
                Set.of(WebUser.Role.DEFAULT_USER));
        webUser.setId(1L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                webUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(put("/task/1/start")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));

        verify(taskService).startTask(eq(1L), any(WebUser.class));
    }

    @Test
    @DisplayName("PUT task")
    void startTaskShouldFailWhenLimitExceeded() throws Exception {
        when(taskService.startTask(eq(1L), any(WebUser.class)))
                .thenThrow(new IllegalStateException("Превышено значение выполняемых Task"));

        WebUser webUser = new WebUser("testUser", "password",
                Set.of(WebUser.Role.DEFAULT_USER));
        webUser.setId(1L);

        Authentication auth = new UsernamePasswordAuthenticationToken(
                webUser, null, List.of(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(put("/task/1/start")
                        .with(authentication(auth)))
                .andExpect(status().isBadRequest());

        verify(taskService).startTask(eq(1L), any(WebUser.class));
    }






}
