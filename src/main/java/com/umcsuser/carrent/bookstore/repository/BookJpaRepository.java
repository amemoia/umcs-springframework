package com.umcsuser.carrent.bookstore.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.umcsuser.carrent.bookstore.model.Book;

@Repository
public interface BookJpaRepository extends JpaRepository<Book, UUID> {
    Optional<Book> findByIsbn(String isbn);
}