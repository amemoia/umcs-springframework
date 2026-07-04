package com.umcsuser.carrent;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.umcsuser.carrent.models.Book;
import com.umcsuser.carrent.models.Role;
import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.BookJpaRepository;
import com.umcsuser.carrent.repositories.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ObjectProvider<BookJpaRepository> bookJpaRepositoryProvider;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ObjectProvider<BookJpaRepository> bookJpaRepositoryProvider, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.bookJpaRepositoryProvider = bookJpaRepositoryProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        initializeUser("admin", "admin123", Role.ADMIN, "00000000-0000-0000-0000-000000000001");
        initializeUser("user", "user123", Role.USER, "a62399e0-940e-4649-abe1-d928397f4d2b");
        BookJpaRepository bookJpaRepository = bookJpaRepositoryProvider.getIfAvailable();
        if (bookJpaRepository != null) {
            initializeBook(bookJpaRepository, "11111111-1111-1111-1111-111111111111", "Clean Code", "Robert C. Martin", "9780132350884", 149.99, 12);
            initializeBook(bookJpaRepository, "22222222-2222-2222-2222-222222222222", "Effective Java", "Joshua Bloch", "9780134685991", 169.99, 8);
        }
    }

    private void initializeUser(String login, String password, Role role, String id) {
        if (userRepository.getUser(login) == null) {
            User user = new User();
            user.setId(id);
            user.setLogin(login);
            user.setPassword(passwordEncoder.encode(password));
            user.setRole(role);
            userRepository.update(user);
            System.out.println("Initialized user: " + login);
        }
    }

    private void initializeBook(BookJpaRepository bookJpaRepository, String id, String title, String author, String isbn, double price, int stock) {
        if (bookJpaRepository.findById(java.util.UUID.fromString(id)).isEmpty()) {
            Book book = new Book();
            book.setId(id);
            book.setTitle(title);
            book.setAuthor(author);
            book.setIsbn(isbn);
            book.setPrice(java.math.BigDecimal.valueOf(price));
            book.setStock(stock);
            bookJpaRepository.save(book);
            System.out.println("Initialized book: " + title);
        }
    }
}
