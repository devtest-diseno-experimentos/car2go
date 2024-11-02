package com.pe.platform.payment.infrastructure.persistence.jpa;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByProfileId(Long profileId);
}
