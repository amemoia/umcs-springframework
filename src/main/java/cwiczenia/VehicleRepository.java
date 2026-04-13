package cwiczenia;
import java.util.List;


public interface VehicleRepository {
    List<Vehicle> getVehicles();
    Vehicle getVehicle(String id);
    Vehicle add(Vehicle vehicle);
    Vehicle update(Vehicle vehicle);
    boolean remove(String id);
    void save();
    void load();
}