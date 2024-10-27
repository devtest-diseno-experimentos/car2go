package com.pe.platform.vehicle.interfaces.rest.transform;

import com.pe.platform.vehicle.domain.model.commands.UpdateVehicleCommand;
import com.pe.platform.vehicle.interfaces.rest.resources.UpdateVehicleResource;

public class UpdateVehicleCommandFromResourceAssembler {

    public static UpdateVehicleCommand toCommandFromResource(UpdateVehicleResource resource) {
        return new UpdateVehicleCommand(
                resource.name(),
                resource.phone(),
                resource.email(),
                resource.brand(),
                resource.model(),
                resource.color(),
                resource.year(),
                resource.price(),
                resource.transmission(),
                resource.engine(),
                resource.mileage(),
                resource.doors(),
                resource.plate(),
                resource.location(),
                resource.description(),
                resource.image(),
                resource.fuel(),
                resource.speed()
        );
    }
}
