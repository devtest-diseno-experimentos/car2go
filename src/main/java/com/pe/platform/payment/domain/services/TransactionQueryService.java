package com.pe.platform.payment.domain.services;

import com.pe.platform.payment.domain.model.aggregates.Transaction;
import com.pe.platform.payment.domain.model.queries.GetAllTransactionsQuery;
import com.pe.platform.payment.domain.model.queries.GetTransactionByIdQuery;

import java.util.List;
import java.util.Optional;

public interface TransactionQueryService {
    List<Transaction> handle(GetAllTransactionsQuery query);
    Optional<Transaction> handle(GetTransactionByIdQuery query);
}
