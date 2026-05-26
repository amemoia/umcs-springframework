package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserService implements IUserService {
    private final UserRepository userRepository;
    private final RentalService rentalService;

    public UserService(UserRepository userRepository, RentalService rentalService) {
        this.userRepository = userRepository;
        this.rentalService = rentalService;
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

    public void deleteUser(String userId, String adminId) {
        User admin = findById(adminId);
        if (userRepository.removeUser(userId, admin) <= 0) {
            throw new RuntimeException("couldnt remove this user");
        }
    }
}