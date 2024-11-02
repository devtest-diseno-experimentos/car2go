package com.pe.platform.payment.application.internal.services.commandservices;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.domain.model.commands.CreateSubscriptionCommand;
import com.pe.platform.payment.domain.model.commands.UpdateSubscriptionCommand;
import com.pe.platform.payment.domain.services.SubscriptionCommandService;
import com.pe.platform.payment.domain.model.valueobjects.SubscriptionStatus;
import com.pe.platform.payment.infrastructure.persistence.jpa.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SubscriptionCommandServiceImpl implements SubscriptionCommandService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionCommandServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public Optional<Subscription> handle(CreateSubscriptionCommand command) {
        Optional<Subscription> existingSubscription = subscriptionRepository.findByProfileId(command.profileId());
        if (existingSubscription.isPresent()) {
            throw new IllegalStateException("A subscription already exists for this user");
        }

        var subscription = new Subscription(command.price(), command.description(), SubscriptionStatus.PAID, command.profileId());
        subscriptionRepository.save(subscription);
        return Optional.of(subscription);
    }

    @Override
    public Optional<Subscription> handle(UpdateSubscriptionCommand command) {
        Optional<Subscription> subscriptionOpt = subscriptionRepository.findByProfileId(command.profileId());
        if (subscriptionOpt.isEmpty()) {
            throw new IllegalArgumentException("Subscription does not exist for the given profile");
        }

        Subscription subscription = subscriptionOpt.get();
        subscription.setStatus(command.status());
        subscriptionRepository.save(subscription);
        return Optional.of(subscription);
    }
}
