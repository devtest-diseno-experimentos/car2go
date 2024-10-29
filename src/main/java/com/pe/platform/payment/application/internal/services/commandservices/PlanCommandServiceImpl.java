package com.pe.platform.payment.application.internal.services.commandservices;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.payment.domain.model.aggregates.Subscription;
import com.pe.platform.payment.domain.model.commands.CreatePlanCommand;
import com.pe.platform.payment.domain.model.commands.UpdatePlanCommand;
import com.pe.platform.payment.domain.model.entities.Plan;
import com.pe.platform.payment.domain.services.PlanCommandService;
import com.pe.platform.payment.infrastructure.persistence.jpa.PlanRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
public class PlanCommandServiceImpl implements PlanCommandService {

    private final PlanRepository planRepository;

    public PlanCommandServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public Optional<Plan> handle(CreatePlanCommand command) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        boolean hasRequiredRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (hasRequiredRole) {
          Optional<Plan> existingPlan = planRepository.findByName(command.name().toLowerCase());
            if (existingPlan.isPresent()) {
                throw new IllegalStateException("A plan with that name already exists");
            }
            var plan = new Plan(command.name(), command.price());
            planRepository.save(plan);
            return Optional.of(plan);
        } else {
            throw new SecurityException(" Only admins can create a plan");
        }
    }

    @Override
    public Optional<Plan> handle(UpdatePlanCommand command) {
        var plan = planRepository.findById(command.planId());
        if (!plan.isPresent()) {
            throw new IllegalArgumentException("Plan doesn't exist");
        }
        plan.get().setPrice(command.price());
        plan.get().setName(command.name());
        var updatedPlan = planRepository.save(plan.get());
        return Optional.of(updatedPlan);
    }
}
