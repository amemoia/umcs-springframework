package com.umcsuser.carrent.repositories.impl;

import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.jpa.UserJpaRepository;

@Repository
@Profile({"jpa", "cli"})
public class UserRepositoryJpaAdapter implements UserRepository {
    private final UserJpaRepository delegate;

    public UserRepositoryJpaAdapter(UserJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public User getUser(String login) {
        return delegate.findByLogin(login).orElse(null);
    }

    @Override
    public List<User> getUsers() {
        return delegate.findAll();
    }

    @Override
    public User update(User user) {
        return delegate.save(user);
    }

    @Override
    public boolean registerNewUser(String login, String password, String role) {
        if (delegate.findByLogin(login).isPresent()) {
            return false;
        }
        User user = new User(login, password, role);
        delegate.save(user);
        return true;
    }
}
