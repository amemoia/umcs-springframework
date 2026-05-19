package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.HibernateUtil;
import com.umcsuser.carrent.repositories.HibernateSessionRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class HibernateRepositorySupport implements HibernateSessionRepository {
    @Override
    public <T> T withSession(Function<Session, T> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return action.apply(session);
        }
    }

    @Override
    public <T> T withTransaction(Function<Session, T> action) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                T result = action.apply(session);
                tx.commit();
                return result;
            } catch (RuntimeException ex) {
                tx.rollback();
                throw ex;
            }
        }
    }

    @Override
    public void withTransaction(Consumer<Session> action) {
        withTransaction(session -> {
            action.accept(session);
            return null;
        });
    }
}

