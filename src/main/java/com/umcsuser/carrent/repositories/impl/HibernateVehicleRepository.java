package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Vehicle;
import com.umcsuser.carrent.repositories.VehicleRepository;

import java.util.List;
import java.util.UUID;

public class HibernateVehicleRepository extends HibernateRepositorySupport implements VehicleRepository {
    @Override
    public List<Vehicle> getVehicles() {
        return withSession(session -> session.createQuery("from Vehicle", Vehicle.class).list());
    }

    @Override
    public Vehicle getVehicle(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        UUID uuid = UUID.fromString(id);
        return withSession(session -> session.get(Vehicle.class, uuid));
    }

    @Override
    public Vehicle add(Vehicle vehicle) {
        return withTransaction(session -> {
            Vehicle merged = (Vehicle) session.merge(vehicle);
            return merged;
        });
    }

    @Override
    public Vehicle update(Vehicle vehicle) {
        return add(vehicle);
    }

    @Override
    public boolean remove(String id) {
        if (id == null || id.isBlank()) {
            return false;
        }
        UUID uuid = UUID.fromString(id);
        return withTransaction(session -> {
            Vehicle vehicle = session.get(Vehicle.class, uuid);
            if (vehicle == null) {
                return false;
            }
            session.remove(vehicle);
            return true;
        });
    }

    @Override
    public void save() {
    }

    @Override
    public void load() {
    }
}


