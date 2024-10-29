package com.pe.platform.profiles.application.internal.commandservices;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.profiles.domain.model.aggregates.Profile;
import com.pe.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.pe.platform.profiles.domain.services.ProfileCommandService;
import com.pe.platform.profiles.infrastructure.persistence.jpa.repositories.ProfileRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {
    private final ProfileRepository profileRepository;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(CreateProfileCommand command) {

        // Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();


        Optional<Profile> profilesByEmail = profileRepository.findByEmail(command.email());
        if (!profilesByEmail.isEmpty()) {
            throw new IllegalArgumentException("Email already exists");
        }

        var profile = new Profile(command);
        profileRepository.save(profile);

        return Optional.of(profile);
    }
}
