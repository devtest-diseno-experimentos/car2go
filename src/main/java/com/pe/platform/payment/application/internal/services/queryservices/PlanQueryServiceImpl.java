package com.pe.platform.payment.application.internal.services.queryservices;

import com.pe.platform.payment.domain.model.entities.Plan;
import com.pe.platform.payment.domain.model.queries.GetAllPlanQuery;
import com.pe.platform.payment.domain.model.queries.GetPlanByIdQuery;
import com.pe.platform.payment.domain.services.PlanQueryService;
import com.pe.platform.payment.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlanQueryServiceImpl implements PlanQueryService {
    private final PlanRepository planRepository;

    public PlanQueryServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }


    @Override
    public Optional<Plan> handle(GetPlanByIdQuery query) {
        return planRepository.findById(query.id());
    }

    @Override
    public List<Plan> handle(GetAllPlanQuery query) {
        return planRepository.findAll();
    }

}
