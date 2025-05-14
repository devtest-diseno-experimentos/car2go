package com.pe.platform;

// Importaciones necesarias para pruebas unitarias con JUnit y Mockito
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import com.pe.platform.vehicle.domain.model.commands.CreateVehicleCommand;
import com.pe.platform.vehicle.domain.model.commands.UpdateVehicleCommand;
import com.pe.platform.vehicle.domain.model.queries.GetVehicleByIdQuery;
import com.pe.platform.vehicle.domain.model.queries.GetAllVehicleQuery;
import com.pe.platform.vehicle.domain.services.VehicleCommandService;
import com.pe.platform.vehicle.domain.services.VehicleQueryService;
import com.pe.platform.vehicle.interfaces.rest.VehicleController;
import com.pe.platform.vehicle.interfaces.rest.resources.CreateVehicleResource;
import com.pe.platform.vehicle.interfaces.rest.resources.UpdateVehicleResource;
import com.pe.platform.vehicle.interfaces.rest.resources.VehicleResource;
import com.pe.platform.vehicle.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import com.pe.platform.vehicle.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

// Anotación para cargar contexto de Spring Boot durante los tests
@SpringBootTest
class Car2goApplicationTests {

    // Mocks de servicios
    private VehicleCommandService vehicleCommandService;
    private VehicleQueryService vehicleQueryService;
    private VehicleController vehicleController;

    // Objetos compartidos entre pruebas
    private Vehicle defaultVehicle;
    private CreateVehicleResource createVehicleResource;
    private UpdateVehicleResource updateVehicleResource;
    private CreateVehicleCommand createVehicleCommand;
    private UpdateVehicleCommand updateVehicleCommand;

    // Configuración previa a cada test
    @BeforeEach
    void setUp() {
        // Mocks de servicios
        vehicleCommandService = mock(VehicleCommandService.class);
        vehicleQueryService = mock(VehicleQueryService.class);
        // Instancia del controlador con dependencias simuladas
        vehicleController = new VehicleController(vehicleCommandService, vehicleQueryService);

        // Recurso base para crear vehículo
        createVehicleResource = new CreateVehicleResource(
            "Car1", "123456789", "test@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City", "Description", List.of("image1.jpg"),
            "Gasoline", 200
        );

        // Se genera el comando y el vehículo base
        createVehicleCommand = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(createVehicleResource);
        defaultVehicle = new Vehicle(createVehicleCommand);
        defaultVehicle.setId(1);
        defaultVehicle.setProfileId(1L); // Simula que pertenece al usuario 1

        // Recurso base para actualización de vehículo
        updateVehicleResource = new UpdateVehicleResource(
            "UpdatedCar", "987654321", "updated@example.com", "UpdatedBrand", "UpdatedModel", "Blue",
            "2024", 12000.0, "Manual", "V8", 10000, "4", "XYZ789", "UpdatedCity", "UpdatedDescription",
            List.of("updated_image.jpg"), "Diesel", 250, "REVIEWED"
        );

        // Comando de actualización
        updateVehicleCommand = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(updateVehicleResource);
    }

    // Test para crear un vehículo
    @Test
    void testCreateVehicle() {
        when(vehicleCommandService.handle(any(CreateVehicleCommand.class)))
            .thenReturn(Optional.of(defaultVehicle));

        ResponseEntity<VehicleResource> response = vehicleController.createVehicle(createVehicleResource);

        assertNotNull(response);
        assertEquals(201, response.getStatusCodeValue());

        VehicleResource resource = response.getBody();
        assertNotNull(resource);
        assertEquals(1, resource.id());
        assertEquals("Car1", resource.name());
        assertEquals("Brand1", resource.brand());
    }

    // Test para obtener un vehículo por ID
    @Test
    void testGetVehicleById() {
        GetVehicleByIdQuery query = new GetVehicleByIdQuery(1);
        when(vehicleQueryService.handle(eq(query))).thenReturn(Optional.of(defaultVehicle));

        ResponseEntity<VehicleResource> response = vehicleController.getVehicleById(1);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        VehicleResource resource = response.getBody();
        assertNotNull(resource);
        assertEquals(1, resource.id());
        assertEquals("Model1", resource.model());
        assertEquals("City", resource.location());
    }

    // Test para actualizar un vehículo
    @Test
    void testUpdateVehicle() {
        // Aplica la actualización sobre el vehículo por defecto
        defaultVehicle.updateVehicleInfo(updateVehicleCommand);

        // Simula contexto de seguridad (usuario autenticado)
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);
        when(mockUserDetails.getId()).thenReturn(1L); // Usuario dueño del vehículo
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Simula que el servicio retorna el vehículo actualizado
        when(vehicleCommandService.handle(any(UpdateVehicleCommand.class), eq(1)))
            .thenReturn(Optional.of(defaultVehicle));

        ResponseEntity<VehicleResource> response = vehicleController.updateVehicle(1, updateVehicleResource);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        VehicleResource resource = response.getBody();
        assertNotNull(resource);
        assertEquals("UpdatedBrand", resource.brand());
        assertEquals("UpdatedCity", resource.location());

        SecurityContextHolder.clearContext(); // Limpia el contexto de seguridad
    }

    // Test para eliminar un vehículo
    @Test
    void testDeleteVehicle() {
        // Simula contexto de seguridad (usuario autenticado)
        UserDetailsImpl mockUserDetails = mock(UserDetailsImpl.class);
        when(mockUserDetails.getId()).thenReturn(1L);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockUserDetails);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        // Simula ejecución del delete sin error
        doNothing().when(vehicleCommandService).deleteVehicle(eq(1), eq(1L));

        ResponseEntity<Void> response = vehicleController.deleteVehicle(1);

        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(vehicleCommandService, times(1)).deleteVehicle(eq(1), eq(1L));

        SecurityContextHolder.clearContext();
    }

    // Test para obtener todos los vehículos
    @Test
    void testGetAllVehicles() {
        // Segundo vehículo simulado
        Vehicle vehicle2 = new Vehicle(new CreateVehicleCommand(
            "Car2", "owner2", "email2@example.com", "Brand2", "Model2", "Blue", "2024", 15000.0,
            "Manual", "V8", 8000, "2", "DEF456", "City2", "Description2", List.of("image2.jpg"),
            "Diesel", 220
        ));
        vehicle2.setId(2);

        // Simula respuesta del servicio
        when(vehicleQueryService.handle(any(GetAllVehicleQuery.class)))
            .thenReturn(List.of(defaultVehicle, vehicle2));

        ResponseEntity<List<VehicleResource>> response = vehicleController.getAllVehicles();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<VehicleResource> resources = response.getBody();
        assertNotNull(resources);
        assertEquals(2, resources.size());

        // Validación parcial de resultados
        VehicleResource v1 = resources.get(0);
        assertEquals("Car1", v1.name());

        VehicleResource v2 = resources.get(1);
        assertEquals("Car2", v2.name());
        assertEquals("Brand2", v2.brand());
    }
}
