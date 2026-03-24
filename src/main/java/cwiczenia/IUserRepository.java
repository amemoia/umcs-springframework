package cwiczenia;

import java.util.List;

public interface IUserRepository {
    User getUser(String login);
    List<User> getUsers();
    boolean update(User user);
    void save();
    void load();
    boolean registerNewUser(String login, String password, String role);
    int removeUser(String login, User admin);
}