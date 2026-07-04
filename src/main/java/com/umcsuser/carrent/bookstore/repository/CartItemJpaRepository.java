package com.umcsuser.carrent.bookstore.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umcsuser.carrent.bookstore.model.CartItem;

@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByUser_LoginOrderByCreatedAtDesc(String login);

    Optional<CartItem> findByUser_LoginAndBook_Id(String login, UUID bookId);

    void deleteByUser_LoginAndBook_Id(String login, UUID bookId);

    void deleteByUser_Login(String login);
}