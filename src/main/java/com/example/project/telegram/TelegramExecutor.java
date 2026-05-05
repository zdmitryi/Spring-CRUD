package com.example.project.telegram;

import com.example.project.models.Task;
import com.example.project.utilities.TaskRepository;
import com.example.project.utilities.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@Component
public class TelegramExecutor {
    @Lazy
    private final TelegramManagerBot bot;
    private final TaskService taskService;
    private final TelegramUserRepository telegramUserRepository;
    @Autowired
    public TelegramExecutor(TelegramManagerBot bot, TaskService taskService, TelegramUserRepository telegramUserRepository) {       this.bot = bot;
        this.taskService = taskService;
        this.telegramUserRepository = telegramUserRepository;
    }

    public void handle(Long chatId, String message) throws TelegramApiException {
        if (bot.getPipelineStep(chatId) == null){
            handleCommand(chatId, message);
        } else
            handleStep(chatId, message);
    }

    public void handleCommand(Long chatId, String message) throws TelegramApiException {
        switch (message){
            case "/start":
                handleStart(chatId);
                break;
            case "/help":
                handleHelp(chatId);
                break;
            case "/create":
                handleCreate(chatId);
                break;
            case "/delete":
                handleDelete(chatId);
                break;
            case "/patch":
                handlePatch(chatId);
                break;
            case "/tasks":
                handleTasks(chatId);
                break;
            default:
                handleUnknown(chatId);
        }
    }
    
    public void handleUnknown(Long chatId) throws TelegramApiException {
        bot.sendMessage(chatId, "Введена неизвестная команда, попробуйте еще раз");
    }


    public void handlePatch(Long chatId) throws TelegramApiException {
        bot.setPipelineStep(chatId, "requirePatchedId");
        bot.sendMessage(chatId, "Введите id задачи для изменения: ");
    }


    public void handleDelete(Long chatId) throws TelegramApiException {
        bot.setPipelineStep(chatId, "requireDelete");
        bot.sendMessage(chatId, "Введите id задачи для удаления: ");
    }



    public void handleStep(Long chatId, String data) throws TelegramApiException {
        String step = bot.getPipelineStep(chatId);

        switch (step) {
            case "requireName" -> {
                bot.saveData(chatId, "taskName", data);
                bot.setPipelineStep(chatId, "requireStartDate");
                bot.sendMessage(chatId, "Введите дату начала (ГГГГ-ММ-ДД):");
            }
            case "requireStartDate" -> {
                bot.saveData(chatId, "startDate", data + "T00:00:00");
                bot.setPipelineStep(chatId, "requireDeadlineDate");
                bot.sendMessage(chatId, "Введите дедлайн (ГГГГ-ММ-ДД):");
            }
            case "requireDeadlineDate" -> {
                bot.saveData(chatId, "deadlineDate", data + "T23:59:59");
                bot.setPipelineStep(chatId, "requirePriority");
                bot.sendMessage(chatId, "Введите приоритет (LOW/MEDIUM/HIGH):");
            }
            case "requirePriority" -> {
                bot.saveData(chatId, "priority", data.toUpperCase());
                createTask(chatId);
            }
            case "requireDelete" -> {
                TelegramUser user = telegramUserRepository.findById(chatId).orElseThrow(() -> new AccessDeniedException("Неавторизованный пользователь"));
                Task task = taskService.deleteTaskById(Integer.getInteger(data), user);
                if (task == null){
                    bot.sendMessage(chatId, "Задача успешно удалена!");
                }
                bot.clearPipeline(chatId);
            }
            case "requirePatchedId" -> {
                bot.saveData(chatId, "patchedId", data);
                bot.setPipelineStep(chatId, "patchName");
                bot.sendMessage(chatId, "Введите имя задачи:");
            }
            case "patchName" -> {
                bot.saveData(chatId, "patchedTaskName", data);
                bot.setPipelineStep(chatId, "patchStartDate");
                bot.sendMessage(chatId, "Введите дату начала (ГГГГ-ММ-ДД):");
            }
            case "patchStartDate" -> {
                bot.saveData(chatId, "patchedStartDate", data + "T00:00:00");
                bot.setPipelineStep(chatId, "patchDeadlineDate");
                bot.sendMessage(chatId, "Введите дедлайн (ГГГГ-ММ-ДД):");
            }
            case "patchDeadlineDate" -> {
                bot.saveData(chatId, "patchedDeadLine", data + "T00:00:00");
                bot.setPipelineStep(chatId, "patchPriority");
                bot.sendMessage(chatId, "Введите приоритет (LOW/MEDIUM/HIGH):");
            }
            case "patchPriority" -> {
                bot.saveData(chatId, "patchedPriority", data.toUpperCase());
                patchTask(chatId);
            }
        }
    }

    public void patchTask(Long chatId) throws TelegramApiException {
        Map<String, String> data = bot.getSavedData(chatId);
        long taskId = Long.parseLong(data.get("patchedId"));
        String taskName = data.get("patchedTaskName");
        String startDate = data.get("patchedStartDate");
        String deadlineDate = data.get("patchedDeadLine");
        String priority = data.get("patchedPriority");
        Task task = new Task(
                null,
                taskName,
                chatId,
                null,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(deadlineDate),
                Task.Priority.valueOf(priority.toUpperCase())
        );
        TelegramUser user = telegramUserRepository.findById(chatId).orElseThrow(() -> new AccessDeniedException("Неавторизованный пользователь"));
        taskService.deleteTaskById(taskId, user);
        taskService.putTaskById(taskId, task, user);
        bot.clearPipeline(chatId);

        bot.sendMessage(chatId, "Задача '" + taskName + "' изменена!");

    }




    public void createTask(Long chatId) throws TelegramApiException {
        Map<String, String> data = bot.getSavedData(chatId);
        String taskName = data.get("taskName");
        String startDate = data.get("startDate");
        String deadlineDate = data.get("deadlineDate");
        String priority = data.get("priority");
        Task task = new Task(
                null,
                taskName,
                chatId,
                null,
                LocalDateTime.parse(startDate),
                LocalDateTime.parse(deadlineDate),
                Task.Priority.valueOf(priority.toUpperCase())
        );
        TelegramUser user = telegramUserRepository.findById(chatId).orElseThrow(() -> new AccessDeniedException("Неавторизованный пользователь"));
        taskService.createTask(task, user);
        bot.clearPipeline(chatId);

        bot.sendMessage(chatId, "Задача '" + taskName + "' создана!");
    }

    public void register(Long chatId, String username){
        telegramUserRepository.save(new TelegramUser(
           chatId,
           username
        ));
    }

    public boolean isRegistered(Long chatId){
        return telegramUserRepository.existsById(chatId);
    }

    private void handleStart(Long chatId) throws TelegramApiException {
        String welcomeMessage = """
                Добро пожаловать в Task Manager!
                Введите /help для просмотра доступных команд.
                """;
        bot.sendMessage(chatId, welcomeMessage);
    }

    private void handleTasks(Long chatId) throws TelegramApiException {
        List<Task> tasks = taskService.getAllFilterTasks(new TaskRepository.TaskSearchFilter(
                null, null, null, null, 5, 0
                ));

        if (tasks.isEmpty()) {
            bot.sendMessage(chatId, "Пока нет задач.");
            return;
        }

        StringBuilder sb = new StringBuilder("Задачи:\n\n");

        for (int i = 0; i < tasks.size(); i++) {
            Task task = tasks.get(i);
            sb.append(i + 1).append(". ").append(task.name());

            if (task.priority() != null) {
                sb.append(" [").append(task.priority()).append("]");
            }
            if (task.deadlineDate() != null) {
                sb.append("\n ").append(task.deadlineDate());
            }
            sb.append("\n\n");
        }
        bot.sendMessage(chatId, sb.toString());
    }


    private void handleHelp(Long chatId) throws TelegramApiException {
        String helpMessage = """
                Справка по командам:
                            /start - Начать работу
                            /help - Эта справка
                            /tasks - Показать все задачи
                            /patch - Изменить задачу
                            /create - Создать новую задачу
                """;
        bot.sendMessage(chatId, helpMessage);
    }

    private void handleCreate(Long chatId) throws TelegramApiException {
        bot.setPipelineStep(chatId, "requireName");
        bot.sendMessage(chatId, "Введите название задачи:");
    }
}
