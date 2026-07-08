package com.umcsuser.carrent.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.models.CartItem;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.BookJpaRepository;
import com.umcsuser.carrent.repositories.CartItemJpaRepository;
import com.umcsuser.carrent.repositories.UserRepository;

@Service
@Transactional
public class CartService {

    private final CartItemJpaRepository cartItemJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final UserRepository userRepository;

    public CartService(CartItemJpaRepository cartItemJpaRepository, BookJpaRepository bookJpaRepository, UserRepository userRepository) {
        this.cartItemJpaRepository = cartItemJpaRepository;
        this.bookJpaRepository = bookJpaRepository;
        this.userRepository = userRepository;
    }

    public List<CartItem> getCart(String login) {
        return cartItemJpaRepository.findByUser_LoginOrderByCreatedAtDesc(login);
    }

    public CartItem addBook(String login, String bookId, int quantity) {
        User user = findUser(login);
        UUID uuid = UUID.fromString(bookId);
        Book book = bookJpaRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Book " + bookId + " does not exist"));
        CartItem cartItem = cartItemJpaRepository.findByUser_LoginAndBook_Id(login, uuid)
                .orElseGet(() -> new CartItem(user, book, 0));
        cartItem.setQuantity(cartItem.getQuantity() + Math.max(quantity, 1));
        return cartItemJpaRepository.save(cartItem);
    }

    public void removeBook(String login, String bookId) {
        cartItemJpaRepository.deleteByUser_LoginAndBook_Id(login, UUID.fromString(bookId));
    }

    public void clearCart(String login) {
        cartItemJpaRepository.deleteByUser_Login(login);
    }

    private User findUser(String login) {
        User user = userRepository.getUser(login);
        if (user == null) {
            throw new RuntimeException("User with login " + login + " doesn't exist");
        }
        return user;
    }
}