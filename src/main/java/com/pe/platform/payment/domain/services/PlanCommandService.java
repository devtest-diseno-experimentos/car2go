package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.commands.CreatePlanCommand;
import com.pe.platform.payment.domain.model.commands.UpdatePlanCommand;
import com.pe.platform.payment.domain.model.entities.Plan;

import java.util.Optional;

public interface PlanCommandService {
    Optional<Plan> handle(CreatePlanCommand command);
    Optional<Plan> handle(UpdatePlanCommand command);
}
