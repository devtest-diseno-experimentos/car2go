package com.pe.platform.payment.interfaces.rest.transform;

import com.pe.platform.payment.domain.model.commands.UpdateTransactionCommand;
import com.pe.platform.payment.interfaces.rest.resources.UpdateTransactionResource;

public class UpdateTransactionCommandFromResourceAssembler {
    public static UpdateTransactionCommand toCommandFromResource(UpdateTransactionResource resource) {
        return new UpdateTransactionCommand(resource.transactionId(), resource.status());
    }
}
