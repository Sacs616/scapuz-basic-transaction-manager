package com.scapuz.msa_accounts.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.scapuz.msa_accounts.domain.exception.AccountNotFoundException;
import com.scapuz.msa_accounts.domain.exception.DomainException;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;
import com.scapuz.msa_accounts.domain.repository.TransactionRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public Transaction execute(Transaction transaction) {
        log.info("Creating transaction for account: {}", transaction.getAccountId());

        transaction.validate();

        if (transactionRepository.existsByTransactionNumber(transaction.getTransactionNumber())) {
            throw new DomainException("Transaction number already exists: " + transaction.getTransactionNumber());
        }

        Account account = accountRepository.findById(transaction.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(transaction.getAccountId()));

        if (!account.isActive()) {
            throw new DomainException("Cannot process transaction for inactive account");
        }

        try {
            switch (transaction.getType()) {
                case DEPOSIT, TRANSFER_IN, INTEREST:
                    account.deposit(transaction.getAmount());
                    break;

                case WITHDRAWAL, TRANSFER_OUT, FEE:
                    if (!account.canWithdraw(transaction.getAmount())) {
                        throw new DomainException("Insufficient funds for transaction");
                    }
                    account.withdraw(transaction.getAmount());
                    break;

                default:
                    throw new DomainException("Unsupported transaction type: " + transaction.getType());
            }
        } catch (DomainException e) {
            log.error("Transaction failed: {}", e.getMessage());
            transaction.fail(e.getMessage());
            transactionRepository.save(transaction);
            throw e;
        }

        accountRepository.save(account);

        Transaction newTransaction = Transaction.builder()
                .accountId(transaction.getAccountId())
                .transactionNumber(transaction.getTransactionNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(Transaction.TransactionStatus.COMPLETED)
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .relatedAccountId(transaction.getRelatedAccountId())
                .createdAt(LocalDateTime.now())
                .processedAt(LocalDateTime.now())
                .build();

        Transaction saved = transactionRepository.save(newTransaction);

        log.info("Transaction completed successfully: {}", saved.getId());
        return saved;
    }
}