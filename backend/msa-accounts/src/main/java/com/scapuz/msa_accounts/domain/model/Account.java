package com.scapuz.msa_accounts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.scapuz.msa_accounts.domain.exception.DomainException;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Integer id;
    private UUID clientId;
    private String accountNumber;
    private AccountType type;
    private AccountStatus status;
    private BigDecimal balance;
    private BigDecimal overdraftLimit;
    private String currency;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    public enum AccountType {
        SAVINGS,
        CHECKING,
        CREDIT,
        INVESTMENT
    }

    public enum AccountStatus {
        ACTIVE,
        INACTIVE,
        BLOCKED,
        CLOSED
    }

    // Business Logic
    public void validate() {
        validateBalance();
        validateOverdraftLimit();
        validateCurrency();
    }

    public Account generateAccountNumber() {
        if (this.type != null) {
            this.accountNumber = String.format("%s-%06d", this.type.name(), this.id);
        }
        return this;
    }

    private void validateBalance() {
        if (balance == null) {
            throw new DomainException("Balance is required");
        }
        if (balance.compareTo(BigDecimal.ZERO) < 0 && type != AccountType.CREDIT) {
            throw new DomainException("Balance cannot be negative for non-credit accounts");
        }
    }

    private void validateOverdraftLimit() {
        if (overdraftLimit != null && overdraftLimit.compareTo(BigDecimal.ZERO) < 0) {
            throw new DomainException("Overdraft limit cannot be negative");
        }
    }

    private void validateCurrency() {
        if (currency == null || !currency.matches("^[A-Z]{3}$")) {
            throw new DomainException("Currency must be a valid 3-letter ISO code (e.g., USD, EUR)");
        }
    }

    public void activate() {
        if (this.status == AccountStatus.CLOSED) {
            throw new DomainException("Cannot activate a closed account");
        }
        this.status = AccountStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        if (this.status == AccountStatus.CLOSED) {
            throw new DomainException("Cannot block a closed account");
        }
        this.status = AccountStatus.BLOCKED;
        this.updatedAt = LocalDateTime.now();
    }

    public void close() {
        if (balance.compareTo(BigDecimal.ZERO) != 0) {
            throw new DomainException("Cannot close account with non-zero balance");
        }
        this.status = AccountStatus.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    public boolean canWithdraw(BigDecimal amount) {
        if (!isActive()) {
            return false;
        }
        BigDecimal availableBalance = balance.add(overdraftLimit != null ? overdraftLimit : BigDecimal.ZERO);
        return availableBalance.compareTo(amount) >= 0;
    }

    public void deposit(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Deposit amount must be positive");
        }
        if (!isActive()) {
            throw new DomainException("Cannot deposit to inactive account");
        }
        this.balance = this.balance.add(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Withdrawal amount must be positive");
        }
        if (!canWithdraw(amount)) {
            throw new DomainException("Insufficient funds for withdrawal");
        }
        this.balance = this.balance.subtract(amount);
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getAvailableBalance() {
        return balance.add(overdraftLimit != null ? overdraftLimit : BigDecimal.ZERO);
    }
}
