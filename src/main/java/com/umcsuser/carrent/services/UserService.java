package com.umcsuser.carrent.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAllUsers() {
        return userRepository.getUsers();
    }

    public User findById(String id) {
        User user = userRepository.getUsers().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User  " + id + " doesnt exist?"));
        return user;
    }

    public User findByLogin(String login) {
        User user = userRepository.getUser(login);
        if (user == null) {
            throw new RuntimeException("User with login " + login + " doesn't exist");
        }
        return user;
    }
}