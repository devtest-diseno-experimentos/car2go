package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.entities.Plan;
import com.pe.platform.payment.domain.model.queries.GetAllPlanQuery;
import com.pe.platform.payment.domain.model.queries.GetPlanByIdQuery;

import java.util.List;
import java.util.Optional;

public interface PlanQueryService {
    Optional<Plan> handle(GetPlanByIdQuery query);
    List<Plan> handle(GetAllPlanQuery query);
}
