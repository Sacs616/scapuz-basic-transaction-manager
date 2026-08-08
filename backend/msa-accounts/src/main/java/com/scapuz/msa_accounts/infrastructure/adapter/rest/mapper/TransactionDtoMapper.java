package com.scapuz.msa_accounts.infrastructure.adapter.rest.mapper;

import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.TransactionRequest;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.TransactionResponse;

@Component
public class TransactionDtoMapper {

    public Transaction toDomain(TransactionRequest request) {
        return Transaction.builder()
                .accountId(request.getAccountId())
                .transactionNumber(request.getTransactionNumber())
                .type(Transaction.TransactionType.valueOf(request.getType()))
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .referenceNumber(request.getReferenceNumber())
                .relatedAccountId(request.getRelatedAccountId())
                .status(Transaction.TransactionStatus.PENDING)
                .build();
    }

    public TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .transactionNumber(transaction.getTransactionNumber())
                .type(transaction.getType().name())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(transaction.getStatus().name())
                .description(transaction.getDescription())
                .referenceNumber(transaction.getReferenceNumber())
                .relatedAccountId(transaction.getRelatedAccountId())
                .createdAt(transaction.getCreatedAt())
                .processedAt(transaction.getProcessedAt())
                .build();
    }
}