package com.umcsuser.carrent;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

public class Authentication {
    private final UserRepository userRepository;

    public Authentication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }
}
