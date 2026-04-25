package com.example.project.telegram;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TelegramManagerBot extends TelegramLongPollingBot {
    private final Logger log = LoggerFactory.getLogger(TelegramManagerBot.class);

    private final Map<Long, String> pipeline = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, String>> saved = new ConcurrentHashMap<>();

    public String getPipelineStep(Long chatId) {
        return pipeline.get(chatId);
    }

    public void setPipelineStep(Long chatId, String step) {
        pipeline.put(chatId, step);
    }

    public void clearPipeline(Long chatId) {
        pipeline.remove(chatId);
        saved.remove(chatId);
    }

    public Map<String, String> getSavedData(Long chatId) {
        return saved.get(chatId);
    }

    public void saveData(Long chatId, String dataName, String data) {
        saved.computeIfAbsent(chatId, k -> new ConcurrentHashMap<>()).put(dataName, data);
    }

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    private final TelegramExecutor telegramExecutor;

    @Autowired
    public TelegramManagerBot (TelegramExecutor telegramExecutor){
        this.telegramExecutor = telegramExecutor;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()){
            String message = update.getMessage().getText();
            String username = update.getMessage().getFrom().getUserName();
            Long chatId = update.getMessage().getChatId();
            log.info("Получено сообщение от {} : {}",
                    update.getMessage().getFrom().getUserName(), message);
        try {
            if (!telegramExecutor.isRegistered(chatId)){
                telegramExecutor.register(chatId, username);
            }
            telegramExecutor.handleCommand(chatId, message);
            } catch (TelegramApiException e) {
            log.error("Telegram API Exception: {}", e.getMessage());
            sendMessageSafe(chatId, "Ошибка. Попробуйте позже.");
        } catch (Exception e) {
            log.error("Error: {}: {}", chatId, e.getMessage());
            sendMessageSafe(chatId, "Что-то пошло не так.");
        }
        }
    }


    public void sendMessageSafe(Long chatId, String text) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId.toString());
            message.setText(text);
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке ответа: {}", e.getMessage());
        }
    }

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        execute(message);
    }

    @Override
    public String getBotToken(){
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }
}
