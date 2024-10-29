package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.commands.CreatePlanCommand;
import com.pe.platform.payment.domain.model.commands.UpdatePlanCommand;
import com.pe.platform.payment.interfaces.rest.resources.CreatePlanResource;
import com.pe.platform.payment.interfaces.rest.resources.UpdatePlanResource;

public class UpdatePlanCommandFromResourceAssembler {
    public static UpdatePlanCommand toCommandFromResource (UpdatePlanResource resource) {

        return new UpdatePlanCommand (
                resource.planId(),
                resource.name(),
                resource.price());
    }
}
