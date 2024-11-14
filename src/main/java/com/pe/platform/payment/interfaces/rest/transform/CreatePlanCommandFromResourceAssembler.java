package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.commands.CreatePlanCommand;
import com.pe.platform.payment.interfaces.rest.resources.CreatePlanResource;

public class CreatePlanCommandFromResourceAssembler {
    public static CreatePlanCommand toCommandFromResource (CreatePlanResource resource) {

        return new CreatePlanCommand (
                resource.name(),
                resource.price());
    }
}
