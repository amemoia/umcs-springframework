package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(String login, String password, String role) {
        return userRepository.registerNewUser(login, password, role);
    }
}
