package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
@Profile("jdbc")
public class JDBCRentalRepository implements RentalRepository {
    private final DataSource dataSource;

    public JDBCRentalRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public List<Rental> findAll() {
        List<Rental> rentals = new ArrayList<>();
        String sql = "SELECT * FROM rentals";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                rentals.add(mapResultSetToRental(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rentals;
    }

    @Override
    public Optional<Rental> findById(String id) {
        String sql = "SELECT * FROM rentals WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(id));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Rental save(Rental rental) {
        String sql = "INSERT INTO rentals (id, vehicle_id, user_id, rent_date_time, return_date_time) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "vehicle_id = EXCLUDED.vehicle_id, user_id = EXCLUDED.user_id, " +
                     "rent_date_time = EXCLUDED.rent_date_time, return_date_time = EXCLUDED.return_date_time";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(rental.getId()));
            pstmt.setObject(2, java.util.UUID.fromString(rental.getVehicleId()));
            pstmt.setObject(3, java.util.UUID.fromString(rental.getUserId()));
            
            if (rental.getRentDateTime() != null && !rental.getRentDateTime().isBlank()) {
                pstmt.setTimestamp(4, Timestamp.valueOf(rental.getRentDateTime()));
            } else {
                pstmt.setNull(4, Types.TIMESTAMP);
            }
            
            if (rental.getReturnDateTime() != null && !rental.getReturnDateTime().isBlank()) {
                pstmt.setTimestamp(5, Timestamp.valueOf(rental.getReturnDateTime()));
            } else {
                pstmt.setNull(5, Types.TIMESTAMP);
            }
            
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rental;
    }

    @Override
    public void deleteById(String id) {
        String sql = "DELETE FROM rentals WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(id));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteByVehicleId(String vehicleId) {
        String sql = "DELETE FROM rentals WHERE vehicle_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(vehicleId));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        String sql = "SELECT * FROM rentals WHERE vehicle_id = ? AND return_date_time IS NULL";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setObject(1, java.util.UUID.fromString(vehicleId));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapResultSetToRental(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Rental mapResultSetToRental(ResultSet rs) throws SQLException {
        Timestamp rentTs = rs.getTimestamp("rent_date_time");
        Timestamp returnTs = rs.getTimestamp("return_date_time");
        
        return new Rental(
            rs.getString("id"),
            rs.getString("vehicle_id"),
            rs.getString("user_id"),
            rentTs != null ? rentTs.toString().substring(0, 19) : null,
            returnTs != null ? returnTs.toString().substring(0, 19) : null
        );
    }
}