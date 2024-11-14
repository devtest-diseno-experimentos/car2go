package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.domain.model.queries.GetAllSubscriptionQuery;
import com.pe.platform.payment.domain.model.queries.GetSubscriptionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface SubscriptionQueryService {
    List<Subscription> handle(GetAllSubscriptionQuery query);
    Optional<Subscription> handle(GetSubscriptionByIdQuery query);
    Optional<Subscription> getByProfileId(Long profileId);
    List<Subscription> getAllSubscriptions();

}
