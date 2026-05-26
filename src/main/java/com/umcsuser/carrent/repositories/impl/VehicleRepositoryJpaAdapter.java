package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;
import com.umcsuser.carrent.repositories.jpa.VehicleJpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
@Profile("jpa")
public class VehicleRepositoryJpaAdapter implements VehicleRepository {
    private final VehicleJpaRepository delegate;

    public VehicleRepositoryJpaAdapter(VehicleJpaRepository delegate) {
        this.delegate = delegate;
    }

    @Override
    public List<Vehicle> getVehicles() {
        return delegate.findAll();
    }

    @Override
    public Vehicle getVehicle(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        return delegate.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public Vehicle add(Vehicle vehicle) {
        return delegate.save(vehicle);
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        return delegate.save(vehicle);
    }

    @Override
    public boolean remove(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        UUID uuid = UUID.fromString(id);
        if (!delegate.existsById(uuid)) {
            return false;
        }
        delegate.deleteById(uuid);
        return true;
    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
    }
}
