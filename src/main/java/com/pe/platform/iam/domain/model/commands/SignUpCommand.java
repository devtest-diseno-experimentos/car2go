package com.pe.platform.iam.domain.model.commands;

import java.util.List;

import com.pe.platform.iam.domain.model.entities.Role;

public record SignUpCommand(String username, String password, List<Role> roles) {
}