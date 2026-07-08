package com.umcsuser.carrent.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.models.CartItem;
import com.umcsuser.carrent.models.Order;
import com.umcsuser.carrent.models.OrderItem;
import com.umcsuser.carrent.models.OrderStatus;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.services.AuthService;
import com.umcsuser.carrent.services.BookService;
import com.umcsuser.carrent.services.CartService;
import com.umcsuser.carrent.services.OrderService;
import com.umcsuser.carrent.services.PaymentService;
import com.umcsuser.carrent.services.UserService;

@Component
@Profile("cli")
public class CliModeRunner implements CommandLineRunner {

    private final AuthService authService;
    private final BookService bookService;
    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final BufferedReader input = new BufferedReader(new InputStreamReader(System.in));

    private User currentUser;

    public CliModeRunner(
            AuthService authService,
            BookService bookService,
            CartService cartService,
            OrderService orderService,
            PaymentService paymentService,
            UserService userService
    ) {
        this.authService = authService;
        this.bookService = bookService;
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @Override
    public void run(String... args) throws Exception {
        printBanner();
        printHelp();

        while (true) {
            String line = prompt("cli> ");
            if (line == null) {
                break;
            }

            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            try {
                if (handle(trimmed)) {
                    break;
                }
            } catch (Exception e) {
                println("Error: " + e.getMessage());
            }
        }
    }

    private boolean handle(String line) throws IOException {
        String[] parts = line.split("\\s+");
        String command = parts[0].toLowerCase(Locale.ROOT);

        return switch (command) {
            case "help", "?" -> {
                printHelp();
                yield false;
            }
            case "exit", "quit" -> true;
            case "whoami" -> {
                printCurrentUser();
                yield false;
            }
            case "login" -> {
                login();
                yield false;
            }
            case "register" -> {
                register();
                yield false;
            }
            case "checkout" -> {
                printOrder(orderService.checkout(requireCurrentUser().getLogin()));
                yield false;
            }
            case "pay" -> {
                handlePayments(new String[]{"payments"});
                yield false;
            }
            case "logout" -> {
                currentUser = null;
                println("Logged out.");
                yield false;
            }
            case "books" -> {
                handleBooks(parts);
                yield false;
            }
            case "users" -> {
                handleUsers(parts);
                yield false;
            }
            case "cart" -> {
                handleCart(parts);
                yield false;
            }
            case "orders" -> {
                handleOrders(parts);
                yield false;
            }
            case "payments" -> {
                handlePayments(parts);
                yield false;
            }
            default -> {
                println("Unknown command: " + line);
                println("Type 'help' for available commands.");
                yield false;
            }
        };
    }

    private void handleBooks(String[] parts) throws IOException {
        String action = parts.length < 2 ? "list" : parts[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "", "list", "all" -> printBooks(bookService.findAll());
            case "get", "show", "view" -> {
                requireArgs(parts, 3, "books get <id>");
                printBook(bookService.findById(parts[2]));
            }
            case "create", "new", "add" -> createBook();
            case "update", "edit" -> {
                requireAdmin();
                requireArgs(parts, 3, "books update <id>");
                updateBook(parts[2]);
            }
            case "delete", "remove", "del" -> {
                requireAdmin();
                requireArgs(parts, 3, "books delete <id>");
                bookService.delete(parts[2]);
                println("Deleted book " + parts[2]);
            }
            default -> println("Unknown books command. Try: books | books show <id> | books new | books edit <id> | books remove <id>");
        }
    }

    private void handleUsers(String[] parts) {
        requireAdmin();
        String action = parts.length < 2 ? "list" : parts[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "", "list", "all" -> printUsers(userService.findAllUsers());
            case "get", "show", "view" -> {
                requireArgs(parts, 3, "users get <id>");
                printUser(userService.findById(parts[2]));
            }
            case "login", "find" -> {
                requireArgs(parts, 3, "users find <login>");
                printUser(userService.findByLogin(parts[2]));
            }
            default -> println("Unknown users command. Try: users | users show <id> | users find <login>");
        }
    }

    private void handleCart(String[] parts) throws IOException {
        User user = requireCurrentUser();
        String action = parts.length < 2 ? "list" : parts[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "", "list", "show", "view" -> printCart(cartService.getCart(user.getLogin()));
            case "add", "put" -> {
                String bookId = promptRequired("Book id: ");
                int quantity = promptInt("Quantity [1]: ", 1);
                printCartItem(cartService.addBook(user.getLogin(), bookId, quantity));
            }
            case "remove", "delete", "del" -> {
                String bookId = promptRequired("Book id: ");
                cartService.removeBook(user.getLogin(), bookId);
                println("Removed book from cart.");
            }
            case "clear", "empty" -> {
                cartService.clearCart(user.getLogin());
                println("Cart cleared.");
            }
            default -> println("Unknown cart command. Try: cart | cart add | cart remove | cart clear");
        }
    }

    private void handleOrders(String[] parts) throws IOException {
        String action = parts.length < 2 ? "my" : parts[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "", "my", "mine", "list" -> printOrders(orderService.findForUser(requireCurrentUser().getLogin()));
            case "all", "everyone" -> {
                requireAdmin();
                printOrders(orderService.findAll());
            }
            case "get", "show", "view" -> {
                requireArgs(parts, 3, "orders get <id>");
                printOrder(orderService.findById(parts[2]));
            }
            case "checkout" -> printOrder(orderService.checkout(requireCurrentUser().getLogin()));
            case "status", "update" -> {
                requireAdmin();
                requireArgs(parts, 3, "orders status <id>");
                String statusValue = promptRequired("New status (NEW, PAID, PROCESSING, SHIPPED, COMPLETED, CANCELLED): ");
                OrderStatus status = OrderStatus.valueOf(statusValue.toUpperCase(Locale.ROOT));
                printOrder(orderService.updateStatus(parts[2], status));
            }
            default -> println("Unknown orders command. Try: orders | orders show <id> | checkout | orders all");
        }
    }

    private void handlePayments(String[] parts) throws IOException {
        String action = parts.length < 2 ? "create" : parts[1].toLowerCase(Locale.ROOT);

        switch (action) {
            case "", "create", "new" -> {
                String orderId = promptRequired("Order id: ");
                verifyOrderOwnership(orderId);
                printPaymentSession(paymentService.createCheckoutSession(orderId));
            }
            case "get", "show", "view" -> {
                requireArgs(parts, 3, "payments get <orderId>");
                verifyOrderOwnership(parts[2]);
                printPaymentSession(paymentService.getSessionForOrder(parts[2]));
            }
            case "webhook" -> {
                String payload = readBlock("Stripe payload JSON, finish with END on its own line:");
                String signature = promptRequired("Stripe-Signature: ");
                printPaymentConfirmation(paymentService.handleWebhook(payload, signature));
            }
            default -> println("Unknown payments command. Try: pay | payments show <orderId> | payments webhook");
        }
    }

    private void createBook() throws IOException {
        requireAdmin();
        Book book = new Book();
        book.setTitle(promptRequired("Title: "));
        book.setAuthor(promptRequired("Author: "));
        book.setIsbn(promptRequired("ISBN: "));
        book.setPrice(promptBigDecimal("Price: "));
        book.setStock(promptInt("Stock: ", 0));
        book.setDescription(prompt("Description [optional]: "));
        printBook(bookService.create(book));
    }

    private void updateBook(String bookId) throws IOException {
        Book existing = bookService.findById(bookId);
        Book update = new Book();
        update.setTitle(promptWithDefault("Title", existing.getTitle()));
        update.setAuthor(promptWithDefault("Author", existing.getAuthor()));
        update.setIsbn(promptWithDefault("ISBN", existing.getIsbn()));
        update.setPrice(promptBigDecimalWithDefault("Price", existing.getPrice()));
        update.setStock(promptIntWithDefault("Stock", existing.getStock()));
        update.setDescription(promptWithDefault("Description", existing.getDescription()));
        printBook(bookService.update(bookId, update));
    }

    private void login() throws IOException {
        String login = promptRequired("Login: ");
        String password = promptRequired("Password: ");
        User user = authService.login(login, password);
        if (user == null) {
            println("Invalid credentials.");
            return;
        }

        currentUser = userService.findByLogin(user.getLogin());
        println("Logged in as " + currentUser.getLogin() + " (" + currentUser.getRole() + ")");
    }

    private void register() throws IOException {
        String login = promptRequired("Login: ");
        String password = promptRequired("Password: ");
        String role = promptWithDefault("Role", "USER").toUpperCase(Locale.ROOT);
        boolean created = authService.register(login, password, role);
        if (!created) {
            println("User already exists.");
            return;
        }

        currentUser = userService.findByLogin(login);
        println("Registered and logged in as " + currentUser.getLogin() + " (" + currentUser.getRole() + ")");
    }

    private void verifyOrderOwnership(String orderId) {
        User user = requireCurrentUser();
        Order order = orderService.findById(orderId);
        boolean isOwner = user.getLogin().equals(order.getUserLogin());
        if (!isOwner && user.getRole() != Role.ADMIN) {
            throw new RuntimeException("You cannot access this order/payment as " + user.getLogin());
        }
    }

    private void printBanner() {
        println("");
        println("=== Carrent CLI Mode ===");
        println("Web server is disabled in this mode; use the commands below to exercise the app directly.");
        println("");
    }

    private void printHelp() {
        println("Commands:");
        println("  help | exit | quit | whoami | login | register | logout");
        println("  books | books show <id> | books new | books edit <id> | books remove <id>");
        println("  cart | cart add | cart remove | cart clear");
        println("  orders | orders show <id> | checkout | orders all        (admin only)");
        println("  pay | payments show <orderId> | payments webhook");
        println("  users | users show <id> | users find <login>        (admin only)");
        println("");
    }

    private void printCurrentUser() {
        if (currentUser == null) {
            println("No user is logged in.");
            return;
        }

        println("Current user: " + currentUser.getLogin() + " (" + currentUser.getRole() + ")");
    }

    private void printBooks(List<Book> books) {
        if (books.isEmpty()) {
            println("No books found.");
            return;
        }

        println("Books:");
        for (Book book : books) {
            printBook(book);
        }
    }

    private void printBook(Book book) {
        println("- " + book.getId() + " | " + book.getTitle() + " | " + book.getAuthor()
                + " | ISBN " + book.getIsbn() + " | " + book.getPrice() + " | stock " + book.getStock());
        if (book.getDescription() != null && !book.getDescription().isBlank()) {
            println("  " + book.getDescription());
        }
    }

    private void printUsers(List<User> users) {
        if (users.isEmpty()) {
            println("No users found.");
            return;
        }

        println("Users:");
        for (User user : users) {
            printUser(user);
        }
    }

    private void printUser(User user) {
        println("- " + user.getId() + " | " + user.getLogin() + " | " + user.getRole());
    }

    private void printCart(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            println("Cart is empty.");
            return;
        }

        println("Cart:");
        for (CartItem cartItem : cartItems) {
            printCartItem(cartItem);
        }
    }

    private void printCartItem(CartItem cartItem) {
        Book book = cartItem.getBook();
        println("- " + cartItem.getId() + " | " + cartItem.getUserLogin() + " | qty " + cartItem.getQuantity()
                + " | " + book.getTitle() + " (" + book.getId() + ")");
    }

    private void printOrders(List<Order> orders) {
        if (orders.isEmpty()) {
            println("No orders found.");
            return;
        }

        println("Orders:");
        for (Order order : orders) {
            printOrder(order);
        }
    }

    private void printOrder(Order order) {
        println("- " + order.getId() + " | " + order.getUserLogin() + " | " + order.getStatus()
                + " | payment " + order.getPaymentStatus() + " | total " + order.getTotalAmount()
                + " | ref " + order.getPaymentReference());
        for (OrderItem item : order.getItems()) {
            println("  * " + item.getQuantity() + " x " + item.getBook().getTitle() + " @ " + item.getUnitPrice());
        }
    }

    private void printPaymentSession(PaymentService.PaymentSession session) {
        println("Payment session:");
        println("- sessionId: " + session.sessionId());
        println("- orderId: " + session.orderId());
        println("- amount: " + session.amount() + " " + session.currency());
        println("- paymentStatus: " + session.paymentStatus());
        println("- stripeStatus: " + session.stripeStatus());
        println("- checkoutUrl: " + session.checkoutUrl());
        println("- message: " + session.message());
    }

    private void printPaymentConfirmation(PaymentService.PaymentConfirmation confirmation) {
        println("Payment confirmation:");
        println("- sessionId: " + confirmation.sessionId());
        println("- orderId: " + confirmation.orderId());
        println("- status: " + confirmation.status());
        println("- message: " + confirmation.message());
    }

    private User requireCurrentUser() {
        if (currentUser == null) {
            throw new RuntimeException("Login required for this command.");
        }

        return currentUser;
    }

    private void requireAdmin() {
        User user = requireCurrentUser();
        if (user.getRole() != Role.ADMIN) {
            throw new RuntimeException("Admin privileges required for this command.");
        }
    }

    private void requireArgs(String[] parts, int count, String usage) {
        if (parts.length < count) {
            throw new RuntimeException("Usage: " + usage);
        }
    }

    private String prompt(String message) throws IOException {
        println(message);
        return input.readLine();
    }

    private String promptRequired(String message) throws IOException {
        while (true) {
            String value = prompt(message);
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
            println("Value is required.");
        }
    }

    private String promptWithDefault(String label, String defaultValue) throws IOException {
        String value = prompt(label + " [" + (defaultValue == null ? "" : defaultValue) + "]: ");
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return value.trim();
    }

    private int promptInt(String message, int defaultValue) throws IOException {
        while (true) {
            String value = prompt(message);
            if (value == null || value.isBlank()) {
                return defaultValue;
            }

            try {
                return Integer.parseInt(value.trim());
            } catch (NumberFormatException e) {
                println("Enter a valid integer.");
            }
        }
    }

    private int promptIntWithDefault(String label, int defaultValue) throws IOException {
        String value = prompt(label + " [" + defaultValue + "]: ");
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return Integer.parseInt(value.trim());
    }

    private BigDecimal promptBigDecimal(String message) throws IOException {
        while (true) {
            String value = promptRequired(message);
            try {
                return new BigDecimal(value.trim());
            } catch (NumberFormatException e) {
                println("Enter a valid decimal number.");
            }
        }
    }

    private BigDecimal promptBigDecimalWithDefault(String label, BigDecimal defaultValue) throws IOException {
        String value = prompt(label + " [" + defaultValue + "]: ");
        if (value == null || value.isBlank()) {
            return defaultValue;
        }

        return new BigDecimal(value.trim());
    }

    private String readBlock(String heading) throws IOException {
        println(heading);
        StringBuilder builder = new StringBuilder();
        while (true) {
            String line = input.readLine();
            if (line == null || "END".equals(line.trim())) {
                break;
            }
            builder.append(line).append('\n');
        }

        return builder.toString().trim();
    }

    private void println(String message) {
        System.out.println(message);
    }
}
