package com.umcsuser.carrent.web;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.umcsuser.carrent.models.Order;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.OrderService;
import com.umcsuser.carrent.services.PaymentService;
import com.umcsuser.carrent.services.UserService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserService userService;

    public PaymentController(OrderService orderService, PaymentService paymentService, UserService userService) {
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @PostMapping("/{orderId}")
    public PaymentService.PaymentSession createSession(@PathVariable String orderId, @AuthenticationPrincipal UserDetails userDetails) {
        verifyOwnership(orderId, userDetails);
        return paymentService.createCheckoutSession(orderId);
    }

    @GetMapping("/{orderId}")
    public PaymentService.PaymentSession getSession(@PathVariable String orderId, @AuthenticationPrincipal UserDetails userDetails) {
        verifyOwnership(orderId, userDetails);
        return paymentService.getSessionForOrder(orderId);
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok("ok");
    }

    private void verifyOwnership(String orderId, UserDetails userDetails) {
        Order order = orderService.findById(orderId);
        User user = userService.findByLogin(userDetails.getUsername());
        boolean isOwner = user.getLogin().equals(order.getUserLogin());
        boolean isAdmin = user.getRole() != null && user.getRole().name().equals("ADMIN");

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("You cannot access this payment session");
        }
    }
}