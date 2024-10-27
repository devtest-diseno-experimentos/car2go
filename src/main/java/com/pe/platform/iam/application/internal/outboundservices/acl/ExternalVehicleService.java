package com.pe.platform.iam.application.internal.outboundservices.acl;

import com.pe.platform.vehicle.interfaces.acl.VehicleContextFacade;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExternalVehicleService {
    private final VehicleContextFacade vehicleContextFacade;

    public ExternalVehicleService(VehicleContextFacade vehicleContextFacade) {
        this.vehicleContextFacade = vehicleContextFacade;
    }

    public List<Long> fetchVehicleIdByUserId(long userId) {
        return vehicleContextFacade.findByProfileId(userId);
    }
}
