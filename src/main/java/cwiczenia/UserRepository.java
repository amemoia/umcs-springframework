package cwiczenia;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class UserRepository implements IUserRepository {
    private Map<String, User> users = new HashMap<>();
    private File csv = new File("users.csv");

    public UserRepository() {
        load();
    }

    @Override
    public boolean registerNewUser(String login, String password, String role) {
        if (getUser(login) != null) return false;
        String hashed = Authentication.hashPassword(password);
        User newUser = new User(login, hashed, role);
        users.put(login, newUser);
        save();
        return true;
    }

    @Override
    public int removeUser(String login, User admin) {
        if (admin.getRole().equals("USER")) return 1;
        if (getUser(login).getRentedVehicleId() != null) return 2;
        users.remove(login);
        save();
        return 0;
    }

    @Override
    public User getUser(String login) {
        return users.get(login);
    }

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean update(User user) {
        if (users.containsKey(user.getLogin())) {
            users.put(user.getLogin(), user);
            return true;
        }
        return false;
    }

    @Override
    public void save() {
        //w3schools
        try {
            PrintWriter myWriter = new PrintWriter(csv);
            //myWriter.write("Files in Java might be tricky, but it is fun enough!");
            for (User u : users.values()) myWriter.println(u.toCSV());
            myWriter.close();  // must close manually
            System.out.println("user save successful");
        } catch (IOException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }

    public void load() {
        // w3schools
        try (Scanner myReader = new Scanner(csv)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] split = line.split(";");

                User cur;
                cur = new User(split[0], split[1], split[2]);
                if (split.length > 3 && !split[3].equals("null") && !split[3].isEmpty()) {
                    cur.setRentedVehicleId(split[3]);
                }
                users.put(cur.getLogin(), cur);
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("error!");
            e.printStackTrace();
        }
    }
}
