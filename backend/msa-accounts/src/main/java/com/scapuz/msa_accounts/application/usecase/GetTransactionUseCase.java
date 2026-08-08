package com.scapuz.msa_accounts.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.exception.TransactionNotFoundException;
import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.domain.repository.TransactionRepository;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public Transaction executeById(Integer id) {
        log.debug("Fetching transaction by id: {}", id);
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    public Transaction executeByTransactionNumber(String transactionNumber) {
        log.debug("Fetching transaction by number: {}", transactionNumber);
        return transactionRepository.findByTransactionNumber(transactionNumber)
                .orElseThrow(() -> new TransactionNotFoundException(transactionNumber));
    }

    public List<Transaction> executeByAccountId(Integer accountId) {
        log.debug("Fetching transactions for account: {}", accountId);
        return transactionRepository.findByAccountId(accountId);
    }
}