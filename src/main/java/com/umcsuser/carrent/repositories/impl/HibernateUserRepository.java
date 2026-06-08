package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

import java.util.List;

public class HibernateUserRepository extends HibernateRepositorySupport implements UserRepository {
    @Override
    public User getUser(String login) {
        return withSession(session -> session
                .createQuery("select u from User u where u.login = :login", User.class)
                .setParameter("login", login)
                .getSingleResultOrNull());
    }

    @Override
    public List<User> getUsers() {
        return withSession(session -> session.createQuery("from User", User.class).list());
    }

    @Override
    public User update(User user) {
        return withTransaction(session -> {
            User merged = session.merge(user);
            return merged;
        });
    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
    }

    @Override
    public boolean registerNewUser(String login, String password, String role) {
        if (getUser(login) != null) {
            return false;
        }
        User newUser = new User(login, password, role);
        update(newUser);
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

        withTransaction((java.util.function.Consumer<org.hibernate.Session>) session -> session.remove(toRemove));
        return 0;
    }
}



