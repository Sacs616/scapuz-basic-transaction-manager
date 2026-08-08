package com.scapuz.msa_accounts.application.usecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.exception.AccountNotFoundException;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetAccountUseCase {

    private final AccountRepository accountRepository;

    @Cacheable(value = "accounts", key = "#id")
    public Account executeById(Integer id) {
        log.debug("Fetching account by id: {}", id);
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account executeByAccountNumber(String accountNumber) {
        log.debug("Fetching account by number: {}", accountNumber);
        return accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    public List<Account> executeByClientId(UUID clientId) {
        log.debug("Fetching accounts for client: {}", clientId);
        return accountRepository.findByClientId(clientId);
    }

    public List<Account> executeAll() {
        log.debug("Fetching all accounts");
        return accountRepository.findByStatus(Account.AccountStatus.ACTIVE);
    }
}