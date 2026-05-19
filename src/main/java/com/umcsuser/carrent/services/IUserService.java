package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

import java.util.List;

public interface IUserService {
    List<User> findAllUsers();
    User findById(String id);
    void deleteUser(String userId, String adminId);
}

