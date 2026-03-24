package cwiczenia;

public class User {
    private String login;
    private String password;
    private String role;
    private String rentedVehicleId;

    public User(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
        this.rentedVehicleId = null;
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
    public void setRentedVehicleId(String vehicleId) {
        this.rentedVehicleId = vehicleId;
    }

    public String toCSV() {
        String vehicleId = (rentedVehicleId == null || rentedVehicleId.isEmpty()) ? "null" : rentedVehicleId;
        return login+";"+password+";"+role+";"+vehicleId;
    }

}
