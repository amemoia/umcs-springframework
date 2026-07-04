package com.umcsuser.carrent.web;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umcsuser.carrent.models.Order;
import com.umcsuser.carrent.services.OrderService;
import com.umcsuser.carrent.services.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;
    private final PaymentService paymentService;

    public PaymentController(OrderService orderService, PaymentService paymentService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
    }

    @PostMapping("/{orderId}")
    public PaymentService.PaymentResult createSession(@PathVariable String orderId, @AuthenticationPrincipal UserDetails userDetails) {
        Order order = orderService.findById(orderId);
        return paymentService.createCheckoutSession(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orderService.findById(orderId);
    }
}