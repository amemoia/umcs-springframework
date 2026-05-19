package com.umcsuser.carrent.repositories;

import com.umcsuser.carrent.models.User;
import java.util.List;

public interface UserRepository {
    User getUser(String login);
    List<User> getUsers();
    User update(User user);
    void save();
    void load();
    boolean registerNewUser(String login, String password, String role);
    int removeUser(String login, User admin);
}