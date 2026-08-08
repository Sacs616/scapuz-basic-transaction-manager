package com.scapuz.msa_accounts.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.scapuz.msa_accounts.domain.exception.DomainException;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private Integer id;
    private Integer accountId;
    private String transactionNumber;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private TransactionStatus status;
    private String description;
    private String referenceNumber;
    private UUID relatedAccountId;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;

    public enum TransactionType {
        DEPOSIT,
        WITHDRAWAL,
        TRANSFER_IN,
        TRANSFER_OUT,
        FEE,
        INTEREST,
        REVERSAL
    }

    public enum TransactionStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REVERSED
    }

    public void validate() {
        validateAmount();
        validateCurrency();
        validateType();
    }

    private void validateAmount() {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Transaction amount must be positive");
        }
    }

    private void validateCurrency() {
        if (currency == null || !currency.matches("^[A-Z]{3}$")) {
            throw new DomainException("Currency must be a valid 3-letter ISO code");
        }
    }

    private void validateType() {
        if (type == null) {
            throw new DomainException("Transaction type is required");
        }
        if ((type == TransactionType.TRANSFER_IN || type == TransactionType.TRANSFER_OUT)
                && relatedAccountId == null) {
            throw new DomainException("Related account is required for transfers");
        }
    }

    public void complete() {
        if (this.status != TransactionStatus.PENDING && this.status != TransactionStatus.PROCESSING) {
            throw new DomainException("Only pending or processing transactions can be completed");
        }
        this.status = TransactionStatus.COMPLETED;
        this.processedAt = LocalDateTime.now();
    }

    public void fail(String reason) {
        if (this.status == TransactionStatus.COMPLETED) {
            throw new DomainException("Cannot fail a completed transaction");
        }
        this.status = TransactionStatus.FAILED;
        this.description = (this.description != null ? this.description + " | " : "") + "Failed: " + reason;
        this.processedAt = LocalDateTime.now();
    }

    public void reverse() {
        if (this.status != TransactionStatus.COMPLETED) {
            throw new DomainException("Only completed transactions can be reversed");
        }
        this.status = TransactionStatus.REVERSED;
        this.processedAt = LocalDateTime.now();
    }

    public boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    }

    public boolean isPending() {
        return this.status == TransactionStatus.PENDING;
    }
}
