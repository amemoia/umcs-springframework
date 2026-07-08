package com.umcsuser.carrent.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && BCrypt.checkpw(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public boolean register(String login, String password, String role) {
        String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        return userRepository.registerNewUser(login, hashedPassword, role);
    }
}