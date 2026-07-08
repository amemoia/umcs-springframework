package com.umcsuser.carrent.repositories.jpa;

import java.util.Optional;
import java.util.UUID;

import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umcsuser.carrent.models.User;

@Repository
@Profile({"jpa", "cli"})
public interface UserJpaRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
