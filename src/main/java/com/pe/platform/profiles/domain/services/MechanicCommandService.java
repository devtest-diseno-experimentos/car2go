package com.pe.platform.profiles.domain.services;

import com.pe.platform.profiles.domain.model.aggregates.Mechanic;
import com.pe.platform.profiles.domain.model.commands.CreateMechanicCommand;

import java.util.Optional;

public interface MechanicCommandService {
    Optional<Mechanic> handle(CreateMechanicCommand command);
}
