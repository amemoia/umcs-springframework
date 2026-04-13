package cwiczenia;

import java.util.List;

public interface IRentalRepository {
    List<Rental> getRentals();
    Rental getRental(String id);
    Rental add(Rental rental);
    Rental update(Rental rental);
    boolean remove(String id);
    Rental findByVehicleIdAndReturnDateIsNull(String vehicleId);
    List<Rental> findByUserId(String userId);
    List<Rental> findActiveRentals();
    void save();
    void load();
}

