package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Car;
import com.umcsuser.carrent.models.Motorcycle;
import com.umcsuser.carrent.models.MotorcycleCategory;
import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("jdbc")
public class JDBCVehicleRepository implements VehicleRepository {
    private final DataSource dataSource;

    public JDBCVehicleRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private Connection getConnection() throws SQLException {
        return DataSourceUtils.getConnection(dataSource);
    }

    private void releaseConnection(Connection conn) {
        DataSourceUtils.releaseConnection(conn, dataSource);
    }

    @Override
    public List<Vehicle> getVehicles() {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles";
        Connection conn = null;
        try {
            conn = getConnection();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
        return vehicles;
    }

    @Override
    public Vehicle getVehicle(String id) {
        String sql = "SELECT * FROM vehicles WHERE id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, java.util.UUID.fromString(id));
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToVehicle(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
        return null;
    }

    @Override
    public Vehicle add(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (id, brand, model, year, price, rented, category, plate) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON CONFLICT (id) DO UPDATE SET " +
                     "brand = EXCLUDED.brand, model = EXCLUDED.model, year = EXCLUDED.year, " +
                     "price = EXCLUDED.price, rented = EXCLUDED.rented, category = EXCLUDED.category, plate = EXCLUDED.plate";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                setVehicleParams(pstmt, vehicle);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            releaseConnection(conn);
        }
        return vehicle;
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        return add(vehicle); // ON CONFLICT handles update
    }

    @Override
    public boolean remove(String id) {
        String sql = "DELETE FROM vehicles WHERE id = ?";
        Connection conn = null;
        try {
            conn = getConnection();
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setObject(1, java.util.UUID.fromString(id));
                return pstmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            releaseConnection(conn);
        }
    }

    @Override
    public void save() {
        // JDBC repository saves immediately in add/update/remove
    }

    @Override
    public void load() {
        // JDBC repository loads on demand
    }

    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String brand = rs.getString("brand");
        String model = rs.getString("model");
        int year = rs.getInt("year");
        float price = rs.getFloat("price");
        boolean rented = rs.getBoolean("rented");
        String category = rs.getString("category");
        String plate = rs.getString("plate");

        Vehicle vehicle;
        if ("MOTORCYCLE".equalsIgnoreCase(category)) {
            MotorcycleCategory mc = MotorcycleCategory.A;
            try {
                // Assuming plate or some other field might store more info, but here we just use what we have
                // or if we had a specific column for motorcycle category
            } catch (Exception e) {}
            vehicle = new Motorcycle(id, brand, model, year, price, rented, mc);
        } else {
            vehicle = new Car(id, brand, model, year, price, rented);
        }
        vehicle.setCategory(category);
        if (plate != null) {
            vehicle.addAttribute("plate", plate);
        }
        return vehicle;
    }

    private void setVehicleParams(PreparedStatement pstmt, Vehicle vehicle) throws SQLException {
        pstmt.setObject(1, java.util.UUID.fromString(vehicle.getId()));
        pstmt.setString(2, vehicle.getBrand());
        pstmt.setString(3, vehicle.getModel());
        pstmt.setInt(4, vehicle.getYear());
        pstmt.setFloat(5, vehicle.getPrice());
        pstmt.setBoolean(6, vehicle.isRented());
        pstmt.setString(7, vehicle.getCategory() != null ? vehicle.getCategory() : (vehicle instanceof Car ? "CAR" : "MOTORCYCLE"));
        Object plate = vehicle.getAttributes().get("plate");
        pstmt.setString(8, plate != null ? plate.toString() : null);
    }
}