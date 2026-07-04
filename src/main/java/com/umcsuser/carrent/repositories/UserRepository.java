package com.umcsuser.carrent.repositories;

import java.util.List;

import com.umcsuser.carrent.models.User;

public interface UserRepository {
    User getUser(String login);
    List<User> getUsers();
    User update(User user);
    boolean registerNewUser(String login, String password, String role);
}