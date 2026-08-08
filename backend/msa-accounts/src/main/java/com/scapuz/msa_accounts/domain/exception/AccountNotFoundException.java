package com.scapuz.msa_accounts.domain.exception;

public class AccountNotFoundException extends DomainException {
    public AccountNotFoundException(Integer id) {
        super("Account not found with id: " + id);
    }

    public AccountNotFoundException(String accountNumber) {
        super("Account not found with number: " + accountNumber);
    }
}
