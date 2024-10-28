package com.pe.platform.profiles.domain.services;

import com.pe.platform.profiles.domain.model.aggregates.Mechanic;
import com.pe.platform.profiles.domain.model.queries.GetAllMechanicsQuery;
import com.pe.platform.profiles.domain.model.queries.GetMechanicByIdQuery;

import java.util.Optional;

public interface MechanicQueryService {
    Optional<Mechanic> handle(GetMechanicByIdQuery query);
    Optional<Mechanic> handle(GetAllMechanicsQuery query);
}
