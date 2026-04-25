package com.example.project.telegram;

import com.example.project.models.WebUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramUserRepository extends JpaRepository<TelegramUser, Long> {
    Optional<WebUser> findByUsername(String username);
    boolean existsByUsername(String username);
}
