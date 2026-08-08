package com.scapuz.msa_accounts.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scapuz.msa_accounts.application.port.input.AccountService;
import com.scapuz.msa_accounts.application.usecase.*;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.model.Transaction;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {

    private final CreateAccountUseCase createAccountUseCase;
    private final GetAccountUseCase getAccountUseCase;
    private final UpdateAccountUseCase updateAccountUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionUseCase getTransactionUseCase;
    private final GetTransactionsByDateRangeUseCase getTransactionsByDateRangeUseCase;

    @Override
    public Account createAccount(Account account) {
        return createAccountUseCase.execute(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountById(Integer id) {
        return getAccountUseCase.executeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Account getAccountByNumber(String accountNumber) {
        return getAccountUseCase.executeByAccountNumber(accountNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAccountsByClientId(UUID clientId) {
        return getAccountUseCase.executeByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Account> getAllAccounts() {
        return getAccountUseCase.executeAll();
    }

    @Override
    public Account updateAccount(Integer id, Account account) {
        return updateAccountUseCase.execute(id, account);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {
        return createTransactionUseCase.execute(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public Transaction getTransactionById(Integer id) {
        return getTransactionUseCase.executeById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountId(Integer accountId) {
        return getTransactionUseCase.executeByAccountId(accountId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByAccountIdAndDateRange(
            Integer accountId,
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return getTransactionsByDateRangeUseCase.executeByAccount(accountId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate) {
        return getTransactionsByDateRangeUseCase.executeAll(startDate, endDate);
    }
}