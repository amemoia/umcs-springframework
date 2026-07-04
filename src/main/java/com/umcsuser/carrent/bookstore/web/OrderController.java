package com.umcsuser.carrent.bookstore.web;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umcsuser.carrent.bookstore.model.Order;
import com.umcsuser.carrent.bookstore.model.OrderStatus;
import com.umcsuser.carrent.bookstore.service.OrderService;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.UserService;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<Order> listAll() {
        return orderService.findAll();
    }

    @GetMapping("/my")
    public List<Order> myOrders(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        return orderService.findForUser(user.getLogin());
    }

    @PostMapping("/checkout")
    public Order checkout(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByLogin(userDetails.getUsername());
        return orderService.checkout(user.getLogin());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Order updateStatus(@PathVariable String id, @RequestBody StatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    @GetMapping("/{id}")
    public Order get(@PathVariable String id) {
        return orderService.findById(id);
    }

    public record StatusRequest(OrderStatus status) {
    }
}