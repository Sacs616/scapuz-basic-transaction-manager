package com.scapuz.msa_accounts.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.scapuz.msa_accounts.domain.model.Transaction;

public interface TransactionRepository {
        Transaction save(Transaction transaction);

        Optional<Transaction> findById(Integer id);

        Optional<Transaction> findByTransactionNumber(String transactionNumber);

        List<Transaction> findByAccountId(Integer accountId);

        List<Transaction> findByAccountIdAndStatus(Integer accountId, Transaction.TransactionStatus status);

        boolean existsByTransactionNumber(String transactionNumber);

        List<Transaction> findByAccountIdAndDateRange(Integer accountId, LocalDateTime startDate,
                        LocalDateTime endDate);

        List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
