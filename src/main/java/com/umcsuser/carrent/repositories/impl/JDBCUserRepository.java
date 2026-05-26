package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.User;
import com.umcsuser.carrent.repositories.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("jdbc")
public class JDBCUserRepository implements UserRepository {

    private final DataSource dataSource;

    public JDBCUserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public User getUser(String login) {
        String sql = "SELECT * FROM users WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUser(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public User update(User user) {
        String sql = "INSERT INTO users (id, login, password, role, rented_vehicle_id) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "login = EXCLUDED.login, password = EXCLUDED.password, " +
                     "role = EXCLUDED.role, rented_vehicle_id = EXCLUDED.rented_vehicle_id";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(user.getId()));
            pstmt.setString(2, user.getLogin());
            pstmt.setString(3, user.getPassword());
            pstmt.setString(4, user.getRole().name());
            String rvId = user.getRentedVehicleId();
            if (rvId != null && !rvId.isEmpty() && !"null".equalsIgnoreCase(rvId)) {
                pstmt.setObject(5, java.util.UUID.fromString(rvId));
            } else {
                pstmt.setNull(5, Types.OTHER);
            }
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public void save() {
        // Immediate save
    }

    @Override
    public void load() {
        // Load on demand
    }

    @Override
    public boolean registerNewUser(String login, String password, String role) {
        if (getUser(login) != null) {
            return false;
        }
        User newUser = new User(login, password, role);
        update(newUser);
        return true;
    }

    @Override
    public int removeUser(String login, User admin) {
        if (admin.getRole() != com.umcsuser.carrent.models.Role.ADMIN) {
            return 1;
        }

        User toRemove = getUser(login);
        if (toRemove == null) {
            return 1;
        }

        if (toRemove.getRentedVehicleId() != null && !toRemove.getRentedVehicleId().isEmpty()) {
            return 2;
        }

        String sql = "DELETE FROM users WHERE login = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, login);
            pstmt.executeUpdate();
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 1;
        }
    }

    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        return new User(
            rs.getString("id"),
            rs.getString("login"),
            rs.getString("password"),
            rs.getString("role"),
            rs.getString("rented_vehicle_id")
        );
    }
}