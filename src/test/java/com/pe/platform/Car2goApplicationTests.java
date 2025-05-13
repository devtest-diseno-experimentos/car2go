package com.pe.platform;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import com.pe.platform.vehicle.domain.model.commands.CreateVehicleCommand;
import com.pe.platform.vehicle.domain.model.commands.UpdateVehicleCommand;
import com.pe.platform.vehicle.domain.model.queries.GetVehicleByIdQuery;
import com.pe.platform.vehicle.domain.services.VehicleCommandService;
import com.pe.platform.vehicle.domain.services.VehicleQueryService;
import com.pe.platform.vehicle.interfaces.rest.VehicleController;
import com.pe.platform.vehicle.interfaces.rest.resources.CreateVehicleResource;
import com.pe.platform.vehicle.interfaces.rest.resources.UpdateVehicleResource;
import com.pe.platform.vehicle.interfaces.rest.resources.VehicleResource;
import com.pe.platform.vehicle.interfaces.rest.transform.CreateVehicleCommandFromResourceAssembler;
import com.pe.platform.vehicle.interfaces.rest.transform.UpdateVehicleCommandFromResourceAssembler;
import com.pe.platform.vehicle.domain.model.queries.GetAllVehicleQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class Car2goApplicationTests {

    private VehicleCommandService vehicleCommandService;
    private VehicleQueryService vehicleQueryService;
    private VehicleController vehicleController;

    @BeforeEach
    void setUp() {
        vehicleCommandService = mock(VehicleCommandService.class);
        vehicleQueryService = mock(VehicleQueryService.class);
        vehicleController = new VehicleController(vehicleCommandService, vehicleQueryService);
    }

    @Test
    void testCreateVehicle() {
        // Arrange
        CreateVehicleResource resource = new CreateVehicleResource(
            "Car1", "123456789", "test@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City", "Description", List.of("image1.jpg"),
            "Gasoline", 200
        );
        CreateVehicleCommand command = CreateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        Vehicle vehicle = new Vehicle(command); // Usamos el constructor que acepta CreateVehicleCommand
        vehicle.setId(1);
        when(vehicleCommandService.handle(any(CreateVehicleCommand.class))).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<VehicleResource> response = vehicleController.createVehicle(resource);

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(1, response.getBody().id(), "Vehicle ID should match the expected value");
        verify(vehicleCommandService, times(1)).handle(any(CreateVehicleCommand.class));
    }

    @Test
    void testGetVehicleById() {
        // Arrange
        int vehicleId = 1;
        Vehicle vehicle = new Vehicle(new CreateVehicleCommand(
            "Car1", "123456789", "test@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City", "Description", List.of("image1.jpg"),
            "Gasoline", 200
        )); 
        vehicle.setId(vehicleId);
        GetVehicleByIdQuery query = new GetVehicleByIdQuery(vehicleId);
        when(vehicleQueryService.handle(eq(query))).thenReturn(Optional.of(vehicle));

        // Act
        ResponseEntity<VehicleResource> response = vehicleController.getVehicleById(vehicleId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vehicleId, response.getBody().id());
        verify(vehicleQueryService, times(1)).handle(eq(query));
    }

    @Test
    void testUpdateVehicle() {
        // Arrange
        int vehicleId = 1;
        UpdateVehicleResource resource = new UpdateVehicleResource(
            "UpdatedCar", "987654321", "updated@example.com", "UpdatedBrand", "UpdatedModel", "Blue",
            "2024", 12000.0, "Manual", "V8", 10000, "4", "XYZ789", "UpdatedCity", "UpdatedDescription",
            List.of("updated_image.jpg"), "Diesel", 250, "REVIEWED"
        );
        UpdateVehicleCommand command = UpdateVehicleCommandFromResourceAssembler.toCommandFromResource(resource);
        Vehicle existingVehicle = new Vehicle(new CreateVehicleCommand(
            "Car1", "123456789", "test@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City", "Description", List.of("image1.jpg"),
            "Gasoline", 200
        ));
        existingVehicle.updateVehicleInfo(command); // Usamos el método para actualizar el vehículo
        existingVehicle.setId(vehicleId);
        when(vehicleCommandService.handle(any(UpdateVehicleCommand.class), eq(vehicleId))).thenReturn(Optional.of(existingVehicle));

        // Act
        ResponseEntity<VehicleResource> response = vehicleController.updateVehicle(vehicleId, resource);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(vehicleId, response.getBody().id());
        verify(vehicleCommandService, times(1)).handle(any(UpdateVehicleCommand.class), eq(vehicleId));
    }

    @Test
    void testDeleteVehicle() {
        // Arrange
        int vehicleId = 1;
        doNothing().when(vehicleCommandService).deleteVehicle(eq(vehicleId), anyLong());

        // Act
        ResponseEntity<Void> response = vehicleController.deleteVehicle(vehicleId);

        // Assert
        assertNotNull(response);
        assertEquals(204, response.getStatusCodeValue());
        verify(vehicleCommandService, times(1)).deleteVehicle(eq(vehicleId), anyLong());
    }

	@Test
    void testGetAllVehicles() {
        // Arrange
        CreateVehicleCommand command1 = new CreateVehicleCommand(
            "Car1", "owner1", "email1@example.com", "Brand1", "Model1", "Red", "2023", 10000.0,
            "Automatic", "V6", 5000, "4", "ABC123", "City1", "Description1", List.of("image1.jpg"),
            "Gasoline", 200
        );
        Vehicle vehicle1 = new Vehicle(command1);
        vehicle1.setId(1);

        CreateVehicleCommand command2 = new CreateVehicleCommand(
            "Car2", "owner2", "email2@example.com", "Brand2", "Model2", "Blue", "2024", 15000.0,
            "Manual", "V8", 8000, "2", "DEF456", "City2", "Description2", List.of("image2.jpg"),
            "Diesel", 220
        );
        Vehicle vehicle2 = new Vehicle(command2);
        vehicle2.setId(2);

        when(vehicleQueryService.handle(any(GetAllVehicleQuery.class)))
            .thenReturn(List.of(vehicle1, vehicle2));

        // Act
        ResponseEntity<List<VehicleResource>> response = vehicleController.getAllVehicles();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(200, response.getStatusCodeValue(), "Status code should be 200");
        assertEquals(2, response.getBody().size(), "Response should contain 2 vehicles");
        verify(vehicleQueryService, times(1)).handle(any(GetAllVehicleQuery.class));
    }
}