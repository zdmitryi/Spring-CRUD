package com.example.project.telegram;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.beans.ConstructorProperties;

@Configuration
public class TelegramBotConfiguration {
    private final Logger log = LoggerFactory.getLogger(TelegramBotConfiguration.class);
    @Autowired
    private TelegramManagerBot telegramManagerBot;
    @PostConstruct
    public void init() throws TelegramApiException {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(telegramManagerBot);
            log.info("Success registering bot: " + telegramManagerBot.getBotUsername());
    }
}
