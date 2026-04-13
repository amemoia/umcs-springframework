package cwiczenia;

import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }

    public User login(String login, String password) {
        User user = userRepository.getUser(login);

        if (user == null) {
            return null;
        }

        if (verifyPassword(password, user.getPassword())) {
            return user;
        }

        return null;
    }

    public User register(String login, String password, String role) {
        User existingUser = userRepository.getUser(login);
        if (existingUser != null) {
            return null;
        }
        String hashedPassword = hashPassword(password);
        User newUser = new User(login, hashedPassword, role);
        userRepository.update(newUser);
        return newUser;
    }
}
