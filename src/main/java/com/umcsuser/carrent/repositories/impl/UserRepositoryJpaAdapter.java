package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import com.umcsuser.carrent.repositories.jpa.UserJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("jpa")
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
    public void save() {
    }

    @Override
    public void load() {
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

    @Override
    public int removeUser(String login, User admin) {
        if (admin == null || admin.getRole() != com.umcsuser.carrent.models.Role.ADMIN) {
            return 1;
        }
        User toRemove = getUser(login);
        if (toRemove == null) {
            return 1;
        }
        if (toRemove.getRentedVehicleId() != null && !toRemove.getRentedVehicleId().isEmpty()) {
            return 2;
        }
        delegate.deleteById(UUID.fromString(toRemove.getId()));
        return 0;
    }
}
