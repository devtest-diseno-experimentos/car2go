package com.pe.platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.pe.platform.iam.domain.model.aggregates.User;
import com.pe.platform.iam.domain.model.commands.SignInCommand;
import com.pe.platform.iam.domain.model.commands.SignUpCommand;
import com.pe.platform.iam.domain.model.entities.Role;
import com.pe.platform.iam.domain.model.valueobjects.Roles;
import com.pe.platform.iam.domain.services.UserCommandService;
import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.iam.interfaces.rest.AuthenticationController;
import com.pe.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;
import com.pe.platform.iam.interfaces.rest.resources.SignInResource;
import com.pe.platform.iam.interfaces.rest.resources.SignUpResource;
import com.pe.platform.iam.interfaces.rest.resources.UserResource;
import com.pe.platform.iam.interfaces.rest.transform.AuthenticatedUserResourceFromEntityAssembler;
import com.pe.platform.interaction.domain.model.aggregates.Review;
import com.pe.platform.interaction.domain.services.ReviewCommandService;
import com.pe.platform.interaction.interfaces.ReviewController;
import com.pe.platform.interaction.interfaces.rest.dto.CreateReviewRequest;
import com.pe.platform.interaction.interfaces.rest.dto.ReviewDTO;
import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import com.pe.platform.vehicle.domain.model.commands.CreateVehicleCommand;
import com.pe.platform.vehicle.domain.services.VehicleCommandService;
import com.pe.platform.vehicle.domain.services.VehicleQueryService;
import com.pe.platform.vehicle.interfaces.rest.VehicleController;
import com.pe.platform.vehicle.interfaces.rest.resources.CreateVehicleResource;
import com.pe.platform.vehicle.interfaces.rest.resources.VehicleResource;
import com.pe.platform.vehicle.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;


import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    // --- Servicios y controladores simulados ---
    @Mock
    private UserCommandService userCommandService;

    private VehicleCommandService vehicleCommandService;
    private VehicleQueryService vehicleQueryService;
    private VehicleController vehicleController;

    private ReviewCommandService reviewCommandService;
    private ReviewController reviewController;

    // --- Datos de prueba comunes ---
    private Vehicle defaultVehicle;
    private CreateVehicleResource createVehicleResource;
    private CreateVehicleCommand createVehicleCommand;

    // Controlador a probar
    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        // Mock manual de servicios de vehículos
        vehicleCommandService = mock(VehicleCommandService.class);
        vehicleQueryService = mock(VehicleQueryService.class);
        vehicleController = new VehicleController(vehicleCommandService, vehicleQueryService);
        
        reviewCommandService = mock(ReviewCommandService.class);
        reviewController = new ReviewController(vehicleCommandService, reviewCommandService);
        

        // Recurso base para registrar vehículo
        createVehicleResource = new CreateVehicleResource(
            "Car1", "123456789", "test@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City", "Description",
            List.of("image1.jpg"), "Gasoline", 200
        );

        // Conversión de recurso a comando
        createVehicleCommand = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(createVehicleResource);

        // Objeto de vehículo con ID y usuario asociado
        defaultVehicle = new Vehicle(createVehicleCommand);
        defaultVehicle.setId(1);
        defaultVehicle.setProfileId(1L);
    }

    @Test
    void UserAndVehicleIntegrationTest() {
        // --- Prueba de registro de usuario (Sign Up) ---
        SignUpResource signUpResource = new SignUpResource("TatoKuni", "secret", List.of("ROLE_SELLER"));
        Role roleUser = new Role(Roles.ROLE_SELLER);
        User mockUser = new User("TatoKuni", "secret", List.of(roleUser));

        when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Optional.of(mockUser));

        ResponseEntity<UserResource> signUpResponse = authenticationController.signUp(signUpResource);

        assertEquals(201, signUpResponse.getStatusCodeValue());
        assertNotNull(signUpResponse.getBody());
        assertEquals("TatoKuni", signUpResponse.getBody().username());
        assertEquals(List.of("ROLE_SELLER"), signUpResponse.getBody().roles());

        // --- Prueba de inicio de sesión (Sign In) ---
        SignInResource signInResource = new SignInResource("TatoKuni", "secret");
        String mockToken = "fake-jwt-token";

        AuthenticatedUserResource expectedAuthResource = new AuthenticatedUserResource(1L, "TatoKuni", mockToken);

        when(userCommandService.handle(any(SignInCommand.class)))
            .thenReturn(Optional.of(new ImmutablePair<>(mockUser, mockToken)));

        try (var mocked = mockStatic(AuthenticatedUserResourceFromEntityAssembler.class)) {
            mocked.when(() -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(mockUser, mockToken))
                  .thenReturn(expectedAuthResource);

            ResponseEntity<AuthenticatedUserResource> signInResponse = authenticationController.signIn(signInResource);

            assertEquals(200, signInResponse.getStatusCodeValue());
            assertNotNull(signInResponse.getBody());
            assertEquals("TatoKuni", signInResponse.getBody().username());
            assertEquals("fake-jwt-token", signInResponse.getBody().token());
            assertEquals(1L, signInResponse.getBody().id());
        }

        // --- Prueba de creación de vehículo posterior al login exitoso ---
        when(vehicleCommandService.handle(any(CreateVehicleCommand.class)))
            .thenReturn(Optional.of(defaultVehicle));

        ResponseEntity<VehicleResource> vehicleResponse = vehicleController.createVehicle(createVehicleResource);

        assertEquals(201, vehicleResponse.getStatusCodeValue());
        assertNotNull(vehicleResponse.getBody());
        assertEquals(1, vehicleResponse.getBody().id());
        assertEquals("Car1", vehicleResponse.getBody().name());
        assertEquals("Brand1", vehicleResponse.getBody().brand());
    }
}
