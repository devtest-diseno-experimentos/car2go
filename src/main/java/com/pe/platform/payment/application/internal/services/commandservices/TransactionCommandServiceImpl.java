package com.pe.platform.payment.application.internal.services.commandservices;

import com.pe.platform.payment.domain.model.aggregates.Transaction;
import com.pe.platform.payment.domain.model.commands.CreateTransactionCommand;
import com.pe.platform.payment.domain.model.commands.UpdateTransactionCommand;
import com.pe.platform.payment.domain.services.TransactionCommandService;
import com.pe.platform.payment.infrastructure.persistence.jpa.TransactionRepository;

import java.util.Optional;

public class TransactionCommandServiceImpl implements TransactionCommandService {

    private final TransactionRepository transactionRepository;

    public TransactionCommandServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    public Optional<Transaction> handle(CreateTransactionCommand command) {
        var transaction = new Transaction(command.buyerId(), command.sellerId(), command.vehicleId(), command.amount());
        transactionRepository.save(transaction);
        return Optional.of(transaction);
    }

    @Override
    public Optional<Transaction> handle(UpdateTransactionCommand command) {
        Optional<Transaction> transactionOpt = transactionRepository.findById(command.transactionId());
        if (transactionOpt.isEmpty()) {
            throw new IllegalArgumentException("Transaction does not exist for the given id");
        }

        Transaction transaction = transactionOpt.get();
        transaction.setStatus(command.status());
        transactionRepository.save(transaction);
        return Optional.of(transaction);
    }
}
