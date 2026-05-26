package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;
import com.umcsuser.carrent.repositories.jpa.RentalJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public class RentalRepositoryJpaAdapter implements RentalRepository {
    private final RentalJpaRepository delegate;

    public RentalRepositoryJpaAdapter(RentalJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Rental> findAll() {
        return delegate.findAll();
    }

    @Override
    public Optional<Rental> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        return delegate.findById(UUID.fromString(id));
    }

    @Override
    public Rental save(Rental rental) {
        return delegate.save(rental);
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        delegate.deleteById(UUID.fromString(id));
    }

    @Override
    public void deleteByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }
        delegate.deleteByVehicle_Id(UUID.fromString(vehicleId));
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }
        return delegate.findFirstByVehicle_IdAndReturnDateTimeIsNull(UUID.fromString(vehicleId));
    }
}
