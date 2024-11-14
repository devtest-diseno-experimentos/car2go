package com.pe.platform.payment.infrastructure.persistence.jpa;

import com.pe.platform.payment.domain.model.aggregates.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByBuyerId(Long buyerId);
    List<Transaction> findAllBySellerId(Long sellerId);
    Optional<Transaction> findByIdAndBuyerId(Long transactionId, Long buyerId);
    Optional<Transaction> findByIdAndSellerId(Long transactionId, Long sellerId);
}
