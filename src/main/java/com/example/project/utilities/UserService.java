package com.example.project.utilities;

import com.example.project.models.WebUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService (UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public WebUser register(WebUser webUserToRegister){
        if (userRepository.existsByUsername(webUserToRegister.getUsername())) throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        webUserToRegister.setPassword(passwordEncoder.encode(webUserToRegister.getPassword()));
        if (webUserToRegister.getRoles() == null || webUserToRegister.getRoles().isEmpty()){
            log.info("New user registered");
            webUserToRegister.setRoles(Set.of(com.example.project.models.WebUser.Role.DEFAULT_USER));
            log.info(webUserToRegister.getRoles().toString());
        }
        return userRepository.save(webUserToRegister);
    }
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Пользователя с таким именем не существует"));
    }
}
