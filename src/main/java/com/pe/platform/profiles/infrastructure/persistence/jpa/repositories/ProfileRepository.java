package com.pe.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.pe.platform.profiles.domain.model.aggregates.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByEmail(String email);

    Optional<Profile> findById(Long id);

    Optional<Profile> findByProfileId(long profileId);


    default boolean canAddPaymentMethod(Profile profile) {
        return profile.getPaymentMethods().size() < 3;
    }
}
