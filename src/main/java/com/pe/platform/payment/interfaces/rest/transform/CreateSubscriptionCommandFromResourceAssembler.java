package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.commands.CreateSubscriptionCommand;
import com.pe.platform.payment.interfaces.rest.resources.CreateSubscriptionResource;

public class CreateSubscriptionCommandFromResourceAssembler {
    public static CreateSubscriptionCommand toCommandFromResource (CreateSubscriptionResource resource) {

        return new CreateSubscriptionCommand (
                resource.price(),
                resource.description(),
                resource.paid());
    }

}

