package cwiczenia;

import java.util.List;

public interface IVehicleRepository {
    void rentVehicle(Vehicle v);
    void returnVehicle(Vehicle v);
    List<Vehicle> getVehicles();
    void save();
    void load();
}
