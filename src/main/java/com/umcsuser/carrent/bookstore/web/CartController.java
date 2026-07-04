package com.umcsuser.carrent.bookstore.web;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umcsuser.carrent.bookstore.model.CartItem;
import com.umcsuser.carrent.bookstore.service.CartService;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.UserService;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserService userService;

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @GetMapping
    public List<CartItem> myCart(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        return cartService.getCart(user.getLogin());
    }

    @PostMapping
    public CartItem add(@RequestBody CartRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        return cartService.addBook(user.getLogin(), request.bookId(), request.quantity());
    }

    @DeleteMapping
    public ResponseEntity<Void> remove(@RequestBody CartRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        cartService.removeBook(user.getLogin(), request.bookId());
        return ResponseEntity.noContent().build();
    }

    public record CartRequest(String bookId, int quantity) {
    }
}