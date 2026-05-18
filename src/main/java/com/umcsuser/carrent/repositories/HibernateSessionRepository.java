package com.umcsuser.carrent.repositories;

import org.hibernate.Session;

import java.util.function.Consumer;
import java.util.function.Function;

public interface HibernateSessionRepository {
    <T> T withSession(Function<Session, T> action);
    <T> T withTransaction(Function<Session, T> action);
    void withTransaction(Consumer<Session> action);
}

