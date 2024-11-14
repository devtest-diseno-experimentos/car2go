package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.aggregates.Transaction;
import com.pe.platform.payment.domain.model.commands.CreateTransactionCommand;
import com.pe.platform.payment.domain.model.commands.UpdateTransactionCommand;

import java.util.Optional;

public interface TransactionCommandService {
    Optional<Transaction> handle(CreateTransactionCommand command);
    Optional<Transaction> handle(UpdateTransactionCommand command);
}
