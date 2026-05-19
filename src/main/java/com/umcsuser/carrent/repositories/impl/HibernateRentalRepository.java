package com.umcsuser.carrent.repositories.impl;

import com.umcsuser.carrent.models.Rental;
import com.umcsuser.carrent.repositories.RentalRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class HibernateRentalRepository extends HibernateRepositorySupport implements RentalRepository {
    @Override
    public List<Rental> findAll() {
        return withSession(session -> session.createQuery("from Rental", Rental.class).list());
    }

    @Override
    public Optional<Rental> findById(String id) {
        if (id == null || id.isBlank()) {
            return Optional.empty();
        }
        UUID uuid = UUID.fromString(id);
        return Optional.ofNullable(withSession(session -> session.get(Rental.class, uuid)));
    }

    @Override
    public Rental save(Rental rental) {
        return withTransaction(session -> {
            Rental merged = (Rental) session.merge(rental);
            return merged;
        });
    }

    @Override
    public void deleteById(String id) {
        if (id == null || id.isBlank()) {
            return;
        }
        UUID uuid = UUID.fromString(id);
        withTransaction(session -> {
            Rental rental = session.get(Rental.class, uuid);
            if (rental != null) {
                session.remove(rental);
            }
        });
    }

    @Override
    public void deleteByVehicleId(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return;
        }
        UUID uuid = UUID.fromString(vehicleId);
        withTransaction((java.util.function.Function<org.hibernate.Session, Integer>) session -> session
                .createQuery("delete from Rental r where r.vehicle.id = :vehicleId")
                .setParameter("vehicleId", uuid)
                .executeUpdate());
    }

    @Override
    public Optional<Rental> findByVehicleIdAndReturnDateIsNull(String vehicleId) {
        if (vehicleId == null || vehicleId.isBlank()) {
            return Optional.empty();
        }
        UUID uuid = UUID.fromString(vehicleId);
        Rental rental = withSession(session -> session
                .createQuery("select r from Rental r where r.vehicle.id = :vehicleId and r.returnDateTime is null", Rental.class)
                .setParameter("vehicleId", uuid)
                .setMaxResults(1)
                .uniqueResult());
        return Optional.ofNullable(rental);
    }
}


