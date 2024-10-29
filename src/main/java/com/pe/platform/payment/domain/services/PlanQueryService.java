package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.entities.Plan;
import com.pe.platform.payment.domain.model.queries.GetPlanByIdQuery;

import java.util.Optional;

public interface PlanQueryService {
    Optional<Plan> handle(GetPlanByIdQuery query);
}
