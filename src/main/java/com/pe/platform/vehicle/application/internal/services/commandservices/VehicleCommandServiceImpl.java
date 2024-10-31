package com.pe.platform.vehicle.application.internal.services.commandservices;

import com.pe.platform.vehicle.domain.model.aggregates.Vehicle;
import com.pe.platform.vehicle.domain.model.commands.CreateVehicleCommand;
import com.pe.platform.vehicle.domain.model.commands.UpdateVehicleCommand;
import com.pe.platform.vehicle.domain.services.VehicleCommandService;
import com.pe.platform.vehicle.infrastructure.persistence.jpa.VehicleRepository;
import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class VehicleCommandServiceImpl implements VehicleCommandService {

    private final VehicleRepository vehicleRepository;

    public VehicleCommandServiceImpl(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public Optional<Vehicle> handle(CreateVehicleCommand command) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        if (!userDetails.isSeller()) {
            throw new IllegalStateException("Only authorized users can create a vehicle");
        }

        var vehicleExists = vehicleRepository.findByName(command.name());
        if (!vehicleExists.isEmpty()) {
            throw new IllegalArgumentException("Vehicle with name " + command.name() + " already exists");
        }

        var newVehicle = new Vehicle(command);
        newVehicle.setProfileId(userDetails.getId());
        var createdVehicle = vehicleRepository.save(newVehicle);
        return Optional.of(createdVehicle);
    }

    @Transactional
    @Override
    public Optional<Vehicle> handle(UpdateVehicleCommand command, int vehicleId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        long userId = userDetails.getId();

        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);
        if (vehicleOptional.isEmpty() || vehicleOptional.get().getProfileId() != userId) {
            throw new IllegalStateException("The vehicle does not exist or you do not have permission to update it.");
        }

        Vehicle vehicle = vehicleOptional.get();
        vehicle.updateVehicleInfo(command);
        vehicle.setProfileId(userId);

        var updatedVehicle = vehicleRepository.save(vehicle);
        return Optional.of(updatedVehicle);
    }

    @Override
    @Transactional
    public void deleteVehicle(int vehicleId, long userId) {
        Optional<Vehicle> vehicleOptional = vehicleRepository.findById(vehicleId);

        if (vehicleOptional.isEmpty() || vehicleOptional.get().getProfileId() != userId) {
            throw new IllegalStateException("The vehicle does not exist or you do not have permission to delete it.");
        }

        Vehicle vehicle = vehicleOptional.get();
        vehicleRepository.delete(vehicle);
    }

    @Override
    public Optional<Vehicle> findById(int vehicleId) {
        return vehicleRepository.findById(vehicleId);
    }
}
