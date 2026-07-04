package com.umcsuser.carrent.services;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.umcsuser.carrent.models.Order;
import com.umcsuser.carrent.models.PaymentStatus;

@Service
public class PaymentService {

    public PaymentResult createCheckoutSession(Order order) {
        return new PaymentResult(
                "stripe-test-" + UUID.randomUUID(),
                PaymentStatus.PAID,
                "Stripe test session created for order " + order.getId()
        );
    }

    public record PaymentResult(String paymentReference, PaymentStatus status, String message) {
    }
}