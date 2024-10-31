package com.pe.platform.iam.interfaces.rest.transform;

import com.pe.platform.iam.domain.model.commands.SignInCommand;
import com.pe.platform.iam.interfaces.rest.resources.SignInResource;

public class SignInCommandFromResourceAssembler {
    public static SignInCommand toCommandFromResource(SignInResource signInResource) {
        return new SignInCommand(signInResource.username(), signInResource.password());
    }
}