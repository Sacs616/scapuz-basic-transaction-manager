package com.scapuz.msa_accounts.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scapuz.msa_accounts.domain.model.Account;

public interface AccountRepository {
    Account save(Account account);

    Optional<Account> findById(Integer id);

    Optional<Account> findByAccountNumber(String accountNumber);

    List<Account> findByClientId(UUID clientId);

    List<Account> findByStatus(Account.AccountStatus status);

    void deleteById(Integer id);

    boolean existsByAccountNumber(String accountNumber);
}
