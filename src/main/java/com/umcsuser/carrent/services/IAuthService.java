package com.umcsuser.carrent.services;

import com.umcsuser.carrent.models.User;

public interface IAuthService {
    User login(String login, String password);
    boolean register(String login, String password, String role);
}

