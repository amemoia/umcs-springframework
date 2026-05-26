package com.umcsuser.carrent.repositories.jpa;

import com.umcsuser.carrent.models.Rental;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@Profile("jpa")
public interface RentalJpaRepository extends JpaRepository<Rental, UUID> {
    Optional<Rental> findFirstByVehicle_IdAndReturnDateTimeIsNull(UUID vehicleId);
    void deleteByVehicle_Id(UUID vehicleId);
}
