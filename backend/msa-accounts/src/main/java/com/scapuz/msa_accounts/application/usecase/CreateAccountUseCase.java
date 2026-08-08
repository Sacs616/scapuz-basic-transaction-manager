package com.scapuz.msa_accounts.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.exception.ClientServiceException;
import com.scapuz.msa_accounts.domain.exception.DomainException;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;
import com.scapuz.msa_accounts.infrastructure.adapter.client.ClientServiceClient;
import com.scapuz.msa_accounts.infrastructure.adapter.client.dto.ClientDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateAccountUseCase {

    private final AccountRepository accountRepository;
    private final ClientServiceClient clientServiceClient;

    public Account execute(Account account) {
        log.info("Creating account for client: {}", account.getClientId());

        account.validate();

        if (accountRepository.existsByAccountNumber(account.getAccountNumber())) {
            throw new DomainException("Account number already exists: " + account.getAccountNumber());
        }

        try {
            ClientDTO client = clientServiceClient.getClient(account.getClientId()).join();

            if (!"ACTIVE".equals(client.getStatus())) {
                throw new DomainException("Cannot create account for inactive client: " + client.getClientId());
            }

            log.info("Client validation passed: {}", client.getClientId());

        } catch (ClientServiceException e) {
            log.error("Failed to validate client: {}", e.getMessage());
            throw new DomainException("Unable to validate client. Please try again later.", e);
        }

        Account newAccount = Account.builder()
                .clientId(account.getClientId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType())
                .status(Account.AccountStatus.ACTIVE)
                .balance(account.getBalance() != null ? account.getBalance() : BigDecimal.ZERO)
                .overdraftLimit(account.getOverdraftLimit())
                .currency(account.getCurrency())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Account saved = accountRepository.save(newAccount);

        saved.generateAccountNumber();
        saved = accountRepository.save(saved);

        log.info("Account created successfully: {}", saved.getId());
        return saved;
    }
}