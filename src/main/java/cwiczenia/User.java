package cwiczenia;

public class User {
    private String id;
    private String login;
    private String password;
    private String role;
    private String rentedVehicleId;

    public User(String login, String password, String role) {
        this.id = login;
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = null;
    }

    public User(String id, String login, String password, String role, String rentedVehicleId) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = rentedVehicleId;
    }

    public String getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public String getRentedVehicleId() {
        return rentedVehicleId;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRentedVehicleId(String vehicleId) {
        this.rentedVehicleId = vehicleId;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String toCSV() {
        String vehicleId = (rentedVehicleId == null || rentedVehicleId.isEmpty()) ? "null" : rentedVehicleId;
        return login + ";" + password + ";" + role + ";" + vehicleId;
    }

    @Override
    public String toString() {
        return "User{" +
                "login='" + login + '\'' +
                ", role='" + role + '\'' +
                ", rentedVehicleId='" + rentedVehicleId + '\'' +
                '}';
    }
}
