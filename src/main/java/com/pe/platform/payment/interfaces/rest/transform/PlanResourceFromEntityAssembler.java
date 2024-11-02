package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.domain.model.entities.Plan;
import com.pe.platform.payment.interfaces.rest.resources.PlanResource;
import com.pe.platform.payment.interfaces.rest.resources.SubscriptionResource;

public class PlanResourceFromEntityAssembler {
    public static PlanResource toResourceFromEntity(Plan entity) {
        return new PlanResource(entity.getName(), entity.getPrice());
    }
}
