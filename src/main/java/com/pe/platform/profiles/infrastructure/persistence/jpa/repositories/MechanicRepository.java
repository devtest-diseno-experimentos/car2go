package com.pe.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.pe.platform.profiles.domain.model.aggregates.Mechanic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MechanicRepository  extends JpaRepository<Mechanic, Long> {
    Optional<Mechanic> findByEmail(String email);
    Optional<Mechanic> findByAddress(String address);
}
