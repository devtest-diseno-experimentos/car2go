package com.pe.platform.iam.domain.services;

import java.util.List;
import java.util.Optional;

import com.pe.platform.iam.domain.model.entities.Role;
import com.pe.platform.iam.domain.model.queries.GetAllRolesQuery;
import com.pe.platform.iam.domain.model.queries.GetRoleByNameQuery;

public interface RoleQueryService {
    List<Role> handle(GetAllRolesQuery query);
    Optional<Role> handle(GetRoleByNameQuery query);
}