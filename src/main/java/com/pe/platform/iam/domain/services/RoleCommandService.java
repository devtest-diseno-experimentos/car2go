package com.pe.platform.iam.domain.services;

import com.pe.platform.iam.domain.model.commands.SeedRolesCommand;

public interface RoleCommandService {
    void handle(SeedRolesCommand command);
}