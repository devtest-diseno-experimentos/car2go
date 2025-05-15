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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

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
    // --- Registro de usuario (Sign Up) ---
    User mockUser = new User("TatoKuni", "secret", List.of(new Role(Roles.ROLE_SELLER)));
    when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Optional.of(mockUser));

    authenticationController.signUp(new SignUpResource("TatoKuni", "secret", List.of("ROLE_SELLER")));

    // --- Simulación del resultado del Sign In ---
    String mockToken = "fake-jwt-token";
    AuthenticatedUserResource mockAuthResource = new AuthenticatedUserResource(1L, "TatoKuni", mockToken);

    try (var mocked = mockStatic(AuthenticatedUserResourceFromEntityAssembler.class)) {
        mocked.when(() -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(mockUser, mockToken))
              .thenReturn(mockAuthResource);

        authenticationController.signIn(new SignInResource("TatoKuni", "secret"));
    }

    // --- Creación de vehículo ---
    when(vehicleCommandService.handle(any(CreateVehicleCommand.class)))
        .thenReturn(Optional.of(defaultVehicle));

    ResponseEntity<VehicleResource> vehicleResponse = vehicleController.createVehicle(createVehicleResource);

    assertEquals(201, vehicleResponse.getStatusCodeValue());
    assertNotNull(vehicleResponse.getBody());
    assertEquals(1, vehicleResponse.getBody().id());
    assertEquals("Car1", vehicleResponse.getBody().name());
    assertEquals("Brand1", vehicleResponse.getBody().brand());
}


@Test
void UserAndReviewIntegrationTest() {
    // --- Registro del mecánico ---
    User mockMechanic = new User("MechanicJohn", "securePassword", List.of(new Role(Roles.ROLE_MECHANIC)));
    when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Optional.of(mockMechanic));
    authenticationController.signUp(new SignUpResource("MechanicJohn", "securePassword", List.of("ROLE_MECHANIC")));

    // --- Mock de token e inicio de sesión (signIn) ---
    String mockToken = "mechanic-jwt-token";
    AuthenticatedUserResource mockAuthResource = new AuthenticatedUserResource(1L, "MechanicJohn", mockToken);
    when(userCommandService.handle(any(SignInCommand.class)))
        .thenReturn(Optional.of(new ImmutablePair<>(mockMechanic, mockToken)));

    // --- Mock del ensamblador que convierte User a AuthenticatedUserResource ---
    try (var mocked = mockStatic(AuthenticatedUserResourceFromEntityAssembler.class)) {
        mocked.when(() -> AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(mockMechanic, mockToken))
              .thenReturn(mockAuthResource);
        authenticationController.signIn(new SignInResource("MechanicJohn", "securePassword"));
    }

    // --- Configurar SecurityContext con usuario autenticado (requerido por el controlador) ---
    UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);
    when(mockUserDetails.getId()).thenReturn(mockAuthResource.id()); // ID que usará el controlador

    Authentication authentication = mock(Authentication.class);
    when(authentication.getPrincipal()).thenReturn(mockUserDetails);

    SecurityContext securityContext = mock(SecurityContext.class);
    when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext); // Necesario para que funcione @PreAuthorize y principal

    // --- Preparar datos de la revisión ---
    CreateReviewRequest reviewRequest = new CreateReviewRequest();
    reviewRequest.setApproved(true);
    reviewRequest.setNotes("Great condition");
    reviewRequest.setVehicleId(1);

    // --- Mock del vehículo y revisión ---
    Vehicle vehicle = new Vehicle(createVehicleCommand);
    vehicle.setId(1);
    Review mockReview = new Review(vehicle, String.valueOf(mockAuthResource.id()), "Great condition");

    when(vehicleCommandService.findById(1)).thenReturn(Optional.of(vehicle));
    when(reviewCommandService.getReviewByVehicleId(1)).thenReturn(Optional.empty());
    when(reviewCommandService.createReview(1, "1", "Great condition", true)).thenReturn(mockReview);

    // --- Ejecutar controlador y verificar respuesta ---
    ResponseEntity<?> reviewResponse = reviewController.createReview(reviewRequest);

    assertEquals(200, reviewResponse.getStatusCodeValue());
    ReviewDTO reviewDTO = (ReviewDTO) reviewResponse.getBody();
    assertEquals("Great condition", reviewDTO.getNotes());
    assertEquals("1", reviewDTO.getReviewedBy());
    assertEquals(1, reviewDTO.getVehicle().getId());
}


    
}
