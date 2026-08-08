package com.scapuz.msa_accounts.application.port.input;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.model.Transaction;

public interface AccountService {
    Account createAccount(Account account);

    Account getAccountById(Integer id);

    Account getAccountByNumber(String accountNumber);

    List<Account> getAccountsByClientId(UUID clientId);

    List<Account> getAllAccounts();

    Account updateAccount(Integer id, Account account);

    Transaction createTransaction(Transaction transaction);

    Transaction getTransactionById(Integer id);

    List<Transaction> getTransactionsByAccountId(Integer accountId);

    List<Transaction> getTransactionsByAccountIdAndDateRange(Integer accountId, LocalDateTime startDate,
            LocalDateTime endDate);

    List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
