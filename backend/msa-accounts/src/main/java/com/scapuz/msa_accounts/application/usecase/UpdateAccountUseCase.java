package com.scapuz.msa_accounts.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.exception.AccountNotFoundException;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateAccountUseCase {

    private final AccountRepository accountRepository;

    @CacheEvict(value = "accounts", key = "#id")
    public Account execute(Integer id, Account updatedAccount) {
        log.info("Updating account: {}", id);

        Account existing = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));

        updatedAccount.validate();

        Account accountToSave = Account.builder()
                .id(existing.getId())
                .clientId(existing.getClientId())
                .accountNumber(existing.getAccountNumber())
                .type(updatedAccount.getType())
                .status(updatedAccount.getStatus())
                .balance(existing.getBalance())
                .overdraftLimit(updatedAccount.getOverdraftLimit())
                .currency(existing.getCurrency())
                .createdAt(existing.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        Account saved = accountRepository.save(accountToSave);
        log.info("Account updated successfully: {}", saved.getId());

        return saved;
    }
}