package com.scapuz.msa_accounts.infrastructure.adapter.rest.mapper;

import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.AccountRequest;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.AccountResponse;

@Component
public class AccountDtoMapper {

    public Account toDomain(AccountRequest request) {
        return Account.builder()
                .clientId(request.getClientId())
                .accountNumber(request.getAccountNumber())
                .type(Account.AccountType.valueOf(request.getType()))
                .balance(request.getBalance())
                .overdraftLimit(request.getOverdraftLimit())
                .currency(request.getCurrency())
                .status(request.getStatus() != null ? Account.AccountStatus.valueOf(request.getStatus())
                        : Account.AccountStatus.ACTIVE)
                .build();
    }

    public AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .clientId(account.getClientId())
                .accountNumber(account.getAccountNumber())
                .type(account.getType().name())
                .status(account.getStatus().name())
                .balance(account.getBalance())
                .availableBalance(account.getAvailableBalance())
                .overdraftLimit(account.getOverdraftLimit())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .updatedAt(account.getUpdatedAt())
                .build();
    }
}