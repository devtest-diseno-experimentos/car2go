package com.pe.platform.profiles.interfaces.rest;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.profiles.application.internal.commandservices.ProfileCommandServiceImpl;
import com.pe.platform.profiles.application.internal.queryservices.ProfileQueryServiceImpl;
import com.pe.platform.profiles.domain.model.aggregates.Profile;
import com.pe.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.pe.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.pe.platform.profiles.domain.model.queries.GetAllProfilesQuery;
import com.pe.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.pe.platform.profiles.interfaces.rest.resources.CreateProfileResource;
import com.pe.platform.profiles.interfaces.rest.resources.ProfileResource;
import com.pe.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import com.pe.platform.profiles.interfaces.rest.transform.CreateProfileCommandFromResourceAssembler;
import com.pe.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import com.pe.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/profiles")
public class ProfilesController {
    private final ProfileCommandServiceImpl profileCommandService;
    private final ProfileQueryServiceImpl profileQueryService;

    public ProfilesController(ProfileCommandServiceImpl profileCommandService, ProfileQueryServiceImpl profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @PostMapping
    public ResponseEntity<ProfileResource> createProfile(@RequestBody CreateProfileResource resource){
    CreateProfileCommand createProfileCommand = CreateProfileCommandFromResourceAssembler.toCommandFromResource(resource);
    var profile =profileCommandService.handle(createProfileCommand);
    if(profile.isEmpty()) return ResponseEntity.badRequest().build();
    var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
    return ResponseEntity.ok(profileResource);
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResource>getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        var getProfileByIdQuery = new GetProfileByIdQuery(userDetails.getId());
        var profile =profileQueryService.handle(getProfileByIdQuery);
        if(profile.isEmpty()) return ResponseEntity.notFound().build();
        var profileResource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile.get());
        return ResponseEntity.ok(profileResource);
    }

    @PutMapping("/me/edit")
    public ResponseEntity<ProfileResource>updateProfile(@RequestBody UpdateProfileResource resource){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userId = userDetails.getId();

        UpdateProfileCommand updateProfileCommand = UpdateProfileCommandFromResource.toCommandFromResource(resource);
        Optional<Profile> updatedProfileOptional = profileCommandService.handle(updateProfileCommand);

        return updatedProfileOptional
                .filter(updatedProfile -> updatedProfile.getProfileId() == userId)
                .map(updatedProfile -> ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(updatedProfile)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
