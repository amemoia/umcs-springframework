package com.umcsuser.carrent.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.stripe.Stripe;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;
import com.umcsuser.carrent.models.Order;
import com.umcsuser.carrent.models.OrderItem;
import com.umcsuser.carrent.models.OrderStatus;
import com.umcsuser.carrent.models.PaymentStatus;
import com.umcsuser.carrent.repositories.OrderJpaRepository;

@Service
@Transactional
public class PaymentService {

    private final OrderJpaRepository orderJpaRepository;
    private final String stripeSecretKey;
    private final String stripeWebhookSecret;
    private final String successUrl;
    private final String cancelUrl;
    private final String currency;

    public PaymentService(
            OrderJpaRepository orderJpaRepository,
            @Value("${stripe.secret-key:}") String stripeSecretKey,
            @Value("${stripe.webhook-secret:}") String stripeWebhookSecret,
            @Value("${stripe.success-url:}") String successUrl,
            @Value("${stripe.cancel-url:}") String cancelUrl,
            @Value("${stripe.currency:pln}") String currency
    ) {
        this.orderJpaRepository = orderJpaRepository;
        this.stripeSecretKey = stripeSecretKey;
        this.stripeWebhookSecret = stripeWebhookSecret;
        this.successUrl = successUrl;
        this.cancelUrl = cancelUrl;
        this.currency = currency;
    }

    public PaymentSession createCheckoutSession(String orderId) {
        Order order = loadOrder(orderId);
        if (hasPaymentReference(order)) {
            return mapCheckoutSession(retrieveStripeSession(order.getPaymentReference()), order.getId());
        }

        Session stripeSession = createStripeSession(order);
        order.setPaymentReference(stripeSession.getId());
        order.setPaymentStatus(PaymentStatus.PENDING);
        orderJpaRepository.save(order);
        return mapCheckoutSession(stripeSession, order.getId());
    }

    public PaymentSession createCheckoutSession(Order order) {
        return createCheckoutSession(order.getId());
    }

    public PaymentSession getSessionForOrder(String orderId) {
        Order order = loadOrder(orderId);
        if (!hasPaymentReference(order)) {
            throw new RuntimeException("Payment session for order " + orderId + " does not exist");
        }

        return mapCheckoutSession(retrieveStripeSession(order.getPaymentReference()), orderId);
    }

    public PaymentConfirmation handleWebhook(String payload, String signatureHeader) {
        ensureStripeConfigured();

        try {
            Event event = Webhook.constructEvent(payload, signatureHeader, stripeWebhookSecret);
            String eventType = event.getType();

            if (!"checkout.session.completed".equals(eventType)
                    && !"checkout.session.async_payment_succeeded".equals(eventType)
                    && !"checkout.session.expired".equals(eventType)
                    && !"checkout.session.async_payment_failed".equals(eventType)) {
                return new PaymentConfirmation(null, null, PaymentStatus.PENDING, "Ignored Stripe event: " + eventType);
            }

            StripeObject dataObject = event.getDataObjectDeserializer().getObject()
                    .orElseThrow(() -> new RuntimeException("Unable to deserialize Stripe session payload"));
            Session stripeSession = (Session) dataObject;
            String orderId = extractOrderId(stripeSession);
            Order order = loadOrder(orderId);

            PaymentStatus paymentStatus = mapStripeStatus(stripeSession.getStatus());
            order.setPaymentStatus(paymentStatus);
            order.setStatus(paymentStatus == PaymentStatus.PAID ? OrderStatus.PAID : OrderStatus.CANCELLED);
            order.setPaymentReference(stripeSession.getId());
            orderJpaRepository.save(order);

            return new PaymentConfirmation(
                    stripeSession.getId(),
                    orderId,
                    paymentStatus,
                    "Stripe webhook processed successfully"
            );
        } catch (Exception e) {
            throw new RuntimeException("Invalid Stripe webhook: " + e.getMessage(), e);
        }
    }

    private Session createStripeSession(Order order) {
        ensureStripeConfigured();

        try {
            SessionCreateParams.Builder builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(cancelUrl)
                    .setClientReferenceId(order.getId())
                    .putMetadata("orderId", order.getId())
                    .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD);

            for (OrderItem orderItem : order.getItems()) {
                builder.addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity((long) orderItem.getQuantity())
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency.toLowerCase(Locale.ROOT))
                                                .setUnitAmount(toMinorUnits(orderItem.getUnitPrice()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName(orderItem.getBook().getTitle())
                                                                .setDescription(orderItem.getBook().getDescription())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                );
            }

            return Session.create(builder.build());
        } catch (Exception e) {
            throw new RuntimeException("Unable to create Stripe checkout session: " + e.getMessage(), e);
        }
    }

    private Session retrieveStripeSession(String sessionId) {
        ensureStripeConfigured();

        try {
            return Session.retrieve(sessionId);
        } catch (Exception e) {
            throw new RuntimeException("Unable to retrieve Stripe checkout session: " + e.getMessage(), e);
        }
    }

    private PaymentSession mapCheckoutSession(Session stripeSession, String orderId) {
        BigDecimal amount = stripeSession.getAmountTotal() == null
                ? BigDecimal.ZERO
                : BigDecimal.valueOf(stripeSession.getAmountTotal()).movePointLeft(2);
        PaymentStatus paymentStatus = mapStripeStatus(stripeSession.getStatus());

        return new PaymentSession(
                stripeSession.getId(),
                orderId,
                amount,
                stripeSession.getCurrency() == null ? currency.toUpperCase(Locale.ROOT) : stripeSession.getCurrency().toUpperCase(Locale.ROOT),
                paymentStatus,
                stripeSession.getUrl(),
                "Stripe checkout session created for order " + orderId,
                LocalDateTime.now(),
                paymentStatus == PaymentStatus.PAID ? LocalDateTime.now() : null,
                stripeSession.getStatus()
        );
    }

    private PaymentStatus mapStripeStatus(String stripeStatus) {
        if (stripeStatus == null) {
            return PaymentStatus.PENDING;
        }

        return switch (stripeStatus.toLowerCase(Locale.ROOT)) {
            case "complete" -> PaymentStatus.PAID;
            case "expired" -> PaymentStatus.FAILED;
            default -> PaymentStatus.PENDING;
        };
    }

    private String extractOrderId(Session stripeSession) {
        if (stripeSession.getMetadata() != null && stripeSession.getMetadata().get("orderId") != null) {
            return stripeSession.getMetadata().get("orderId");
        }

        if (stripeSession.getClientReferenceId() != null) {
            return stripeSession.getClientReferenceId();
        }

        throw new RuntimeException("Stripe session does not contain an order reference");
    }

    private Order loadOrder(String orderId) {
        return orderJpaRepository.findById(UUID.fromString(orderId))
                .orElseThrow(() -> new RuntimeException("Order " + orderId + " does not exist"));
    }

    private boolean hasPaymentReference(Order order) {
        return order.getPaymentReference() != null && !order.getPaymentReference().isBlank();
    }

    private void ensureStripeConfigured() {
        if (stripeSecretKey == null || stripeSecretKey.isBlank()) {
            throw new RuntimeException("Stripe secret key is not configured");
        }

        Stripe.apiKey = stripeSecretKey;
    }

    private long toMinorUnits(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).movePointRight(2).longValueExact();
    }

    public record PaymentSession(
            String sessionId,
            String orderId,
            BigDecimal amount,
            String currency,
            PaymentStatus paymentStatus,
            String checkoutUrl,
            String message,
            LocalDateTime createdAt,
            LocalDateTime confirmedAt,
            String stripeStatus
    ) {
    }

    public record PaymentConfirmation(String sessionId, String orderId, PaymentStatus status, String message) {
    }
}