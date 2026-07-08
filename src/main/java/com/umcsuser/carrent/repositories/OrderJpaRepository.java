package com.umcsuser.carrent.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umcsuser.carrent.models.Order;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUser_LoginOrderByCreatedAtDesc(String login);
}