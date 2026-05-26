package com.umcsuser.carrent.repositories.jpa;

import com.umcsuser.carrent.models.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
@Profile("jpa")
public interface VehicleJpaRepository extends JpaRepository<Vehicle, UUID> {
}
