package com.pe.platform.iam.domain.model.queries ;

import com.pe.platform.iam.domain.model.valueobjects.Roles;

public record GetRoleByNameQuery(Roles roleName) {
}