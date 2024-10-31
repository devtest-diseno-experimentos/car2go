package com.pe.platform.profiles.application.internal.commandservices;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.profiles.domain.model.aggregates.Profile;
import com.pe.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.pe.platform.profiles.domain.model.commands.UpdateProfileCommand;
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Check if a profile already exists for the profileId
        Optional<Profile> existingProfile = profileRepository.findByProfileId(userDetails.getId());
        if (!existingProfile.isEmpty()) {
            throw new IllegalArgumentException("A profile already exists for this user");
        }

        Optional<Profile> profilesByEmail = profileRepository.findByEmail(command.email());
        if (!profilesByEmail.isEmpty()) {
            throw new IllegalArgumentException("Email already exists");
        }

        var profile = new Profile(command, userDetails.getId());

        profileRepository.save(profile);

        return Optional.of(profile);
    }

    @Override
    public Optional<Profile> handle(UpdateProfileCommand command) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Optional<Profile> existingProfile = profileRepository.findByProfileId(userDetails.getId());
        if (existingProfile.isEmpty()) {
            throw new IllegalArgumentException("No profile found for this user");
        }

        Profile profile = existingProfile.get();

        profile.updateName(command.firstName(), command.lastName());

        profileRepository.save(profile);

        return Optional.of(profile);
    }
}
