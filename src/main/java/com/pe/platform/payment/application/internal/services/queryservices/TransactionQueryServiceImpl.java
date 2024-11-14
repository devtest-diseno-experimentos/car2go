package com.pe.platform.payment.application.internal.services.queryservices;

import com.pe.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import com.pe.platform.payment.domain.model.aggregates.Transaction;
import com.pe.platform.payment.domain.model.queries.GetAllTransactionsQuery;
import com.pe.platform.payment.domain.model.queries.GetTransactionByIdQuery;
import com.pe.platform.payment.domain.services.TransactionQueryService;
import com.pe.platform.payment.infrastructure.persistence.jpa.TransactionRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionQueryServiceImpl implements TransactionQueryService {

    private final TransactionRepository transactionRepository;

    public TransactionQueryServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> handle(GetAllTransactionsQuery query) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

        boolean hasBuyerRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_BUYER"));
        boolean hasSellerRole = authorities.stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_SELLER"));

        if (hasBuyerRole) {
            List<Transaction> buyerTransactions = transactionRepository.findAllByBuyerId(userDetails.getId());
            return buyerTransactions.isEmpty() ? List.of() : buyerTransactions;
        } else if (hasSellerRole) {
            List<Transaction> sellerTransactions = transactionRepository.findAllBySellerId(userDetails.getId());
            return sellerTransactions.isEmpty() ? List.of() : sellerTransactions;
        }
        
        throw new IllegalArgumentException("User does not have required role");
    }

    @Override
    public Optional<Transaction> handle(GetTransactionByIdQuery query) {
        return transactionRepository.findById(query.id());
    }
}
