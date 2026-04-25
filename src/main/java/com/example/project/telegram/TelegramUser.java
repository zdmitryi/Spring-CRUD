package com.example.project.telegram;

import com.example.project.models.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "telegram_users")
public class TelegramUser implements User {
    @Id
    private Long chatId;

    @Column(name = "username")
    private String username;

    public TelegramUser(){}

    public TelegramUser(Long chatId, String username){
        this.chatId = chatId;
        this.username = username;
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String username){
        this.username = username;
    }

    public void setChatId(Long chatId){
        this.chatId = chatId;
    }

    public Long getChatId(){
        return chatId;
    }

    @Override
    public Long getId() {
        return getChatId();
    }

    @Override
    public boolean isAdmin() {
        return false;
    }
}
