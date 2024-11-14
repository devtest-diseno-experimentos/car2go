package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.interfaces.rest.resources.SubscriptionResource;

public class SubscriptionResourceFromEntityAssembler {
    public static SubscriptionResource toResourceFromEntity(Subscription entity) {
        return new SubscriptionResource(entity.getPrice(), entity.getDescription(), entity.getStatus(), entity.getProfileId());
    }
}
