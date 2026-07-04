package com.umcsuser.carrent.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.repositories.BookJpaRepository;

@Service
@Transactional
public class BookService {

    private final BookJpaRepository bookJpaRepository;

    public BookService(BookJpaRepository bookJpaRepository) {
        this.bookJpaRepository = bookJpaRepository;
    }

    public List<Book> findAll() {
        return bookJpaRepository.findAll();
    }

    public Book findById(String id) {
        return bookJpaRepository.findById(java.util.UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("Book " + id + " does not exist"));
    }

    public Book create(Book book) {
        book.setId(null);
        return bookJpaRepository.save(book);
    }

    public Book update(String id, Book book) {
        Book existing = findById(id);
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        existing.setPrice(book.getPrice());
        existing.setStock(book.getStock());
        existing.setDescription(book.getDescription());
        return bookJpaRepository.save(existing);
    }

    public void delete(String id) {
        bookJpaRepository.deleteById(java.util.UUID.fromString(id));
    }
}