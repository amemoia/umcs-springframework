package com.umcsuser.carrent.repositories.jpa;

import com.umcsuser.carrent.models.User;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
