package cwiczenia;


import org.apache.commons.codec.digest.DigestUtils;

public class Authentication {

    private IUserRepository userRepository;

    public Authentication(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static String hashPassword(String password) {
        return DigestUtils.sha256Hex(password);
    }

    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }

    public User authenticate(String login, String password) {
        User user = userRepository.getUser(login);

        if (user == null)  return null;
        if (verifyPassword(password, user.getPassword())) return user;
        return null;
    }
}


