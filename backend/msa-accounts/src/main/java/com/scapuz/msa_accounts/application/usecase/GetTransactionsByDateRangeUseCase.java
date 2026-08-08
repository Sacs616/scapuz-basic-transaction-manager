package com.scapuz.msa_accounts.application.usecase;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.exception.AccountNotFoundException;
import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;
import com.scapuz.msa_accounts.domain.repository.TransactionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetTransactionsByDateRangeUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public List<Transaction> executeByAccount(
            Integer accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) {

        log.debug("Fetching transactions for account {} between {} and {}",
                accountId, startDate, endDate);

        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(accountId));

        validateDateRange(startDate, endDate);

        List<Transaction> transactions = transactionRepository
                .findByAccountIdAndDateRange(accountId, startDate, endDate);

        log.debug("Found {} transactions for account {} in date range",
                transactions.size(), accountId);

        return transactions;
    }

    public List<Transaction> executeAll(
            LocalDateTime startDate,
            LocalDateTime endDate) {

        log.debug("Fetching all transactions between {} and {}", startDate, endDate);

        validateDateRange(startDate, endDate);

        List<Transaction> transactions = transactionRepository
                .findByDateRange(startDate, endDate);

        log.debug("Found {} transactions in date range", transactions.size());

        return transactions;
    }

    private void validateDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date are required");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException(
                    "Start date must be before or equal to end date");
        }

        long daysBetween = java.time.Duration.between(startDate, endDate).toDays();
        if (daysBetween > 365) {
            throw new IllegalArgumentException(
                    "Date range cannot exceed 365 days. Requested: " + daysBetween + " days");
        }
    }
}