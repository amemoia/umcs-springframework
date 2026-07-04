package com.umcsuser.carrent.bookstore.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.umcsuser.carrent.bookstore.model.Book;
import com.umcsuser.carrent.bookstore.model.CartItem;
import com.umcsuser.carrent.bookstore.model.Order;
import com.umcsuser.carrent.bookstore.model.OrderItem;
import com.umcsuser.carrent.bookstore.model.OrderStatus;
import com.umcsuser.carrent.bookstore.repository.BookJpaRepository;
import com.umcsuser.carrent.bookstore.repository.OrderJpaRepository;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;

@Service
@Transactional
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final BookJpaRepository bookJpaRepository;
    private final CartService cartService;
    private final UserRepository userRepository;
    private final PaymentService paymentService;

    public OrderService(OrderJpaRepository orderJpaRepository, BookJpaRepository bookJpaRepository, CartService cartService, UserRepository userRepository, PaymentService paymentService) {
        this.orderJpaRepository = orderJpaRepository;
        this.bookJpaRepository = bookJpaRepository;
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
    }

    public List<Order> findAll() {
        return orderJpaRepository.findAll();
    }

    public List<Order> findForUser(String login) {
        return orderJpaRepository.findByUser_LoginOrderByCreatedAtDesc(login);
    }

    public Order checkout(String login) {
        User user = findUser(login);
        List<CartItem> cart = cartService.getCart(login);
        if (cart.isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = new Order(user);
        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart) {
            Book book = bookJpaRepository.findById(UUID.fromString(cartItem.getBook().getId()))
                    .orElseThrow(() -> new RuntimeException("Book not found in cart"));
            if (book.getStock() < cartItem.getQuantity()) {
                throw new RuntimeException("Not enough stock for book: " + book.getTitle());
            }
            book.setStock(book.getStock() - cartItem.getQuantity());
            bookJpaRepository.save(book);

            OrderItem orderItem = new OrderItem(book, cartItem.getQuantity(), book.getPrice());
            order.addItem(orderItem);
            total = total.add(book.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotalAmount(total);

        PaymentService.PaymentResult paymentResult = paymentService.createCheckoutSession(order);
        order.setPaymentReference(paymentResult.paymentReference());
        order.setPaymentStatus(paymentResult.status());

        Order saved = orderJpaRepository.save(order);
        cartService.clearCart(login);
        return saved;
    }

    public Order updateStatus(String orderId, OrderStatus status) {
        Order order = findById(orderId);
        order.setStatus(status);
        return orderJpaRepository.save(order);
    }

    public Order findById(String orderId) {
        return orderJpaRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Order " + orderId + " does not exist"));
    }

    private User findUser(String login) {
        User user = userRepository.getUser(login);
        if (user == null) {
            throw new RuntimeException("User with login " + login + " doesn't exist");
        }
        return user;
    }
}