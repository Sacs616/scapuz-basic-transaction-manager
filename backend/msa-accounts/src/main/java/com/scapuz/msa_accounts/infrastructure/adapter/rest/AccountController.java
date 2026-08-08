package com.scapuz.msa_accounts.infrastructure.adapter.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.scapuz.msa_accounts.application.port.input.AccountService;
import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.AccountRequest;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.dto.AccountResponse;
import com.scapuz.msa_accounts.infrastructure.adapter.rest.mapper.AccountDtoMapper;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
@Tag(name = "Account Management", description = "APIs for managing accounts")
public class AccountController {

    private final AccountService accountService;
    private final AccountDtoMapper dtoMapper;

    @PostMapping
    @Operation(summary = "Create account", description = "Creates a new account for a client")
    @ApiResponse(responseCode = "201", description = "Account created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid request data")
    @ApiResponse(responseCode = "503", description = "Client service unavailable")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request) {

        log.info("REST request to create account for client: {}", request.getClientId());

        Account account = dtoMapper.toDomain(request);
        Account created = accountService.createAccount(account);
        AccountResponse response = dtoMapper.toResponse(created);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get account by ID")
    @ApiResponse(responseCode = "200", description = "Account found")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public ResponseEntity<AccountResponse> getAccount(
            @Parameter(description = "Account ID") @PathVariable Integer id) {

        log.info("REST request to get account: {}", id);

        Account account = accountService.getAccountById(id);
        AccountResponse response = dtoMapper.toResponse(account);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get account by number")
    public ResponseEntity<AccountResponse> getAccountByNumber(
            @Parameter(description = "Account number") @PathVariable String accountNumber) {

        log.info("REST request to get account by number: {}", accountNumber);

        Account account = accountService.getAccountByNumber(accountNumber);
        AccountResponse response = dtoMapper.toResponse(account);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "Get accounts by client ID")
    public ResponseEntity<List<AccountResponse>> getAccountsByClientId(
            @Parameter(description = "Client ID") @PathVariable UUID clientId) {

        log.info("REST request to get accounts for client: {}", clientId);

        List<Account> accounts = accountService.getAccountsByClientId(clientId);
        List<AccountResponse> responses = accounts.stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping
    @Operation(summary = "Get all active accounts")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        log.info("REST request to get all accounts");

        List<Account> accounts = accountService.getAllAccounts();
        List<AccountResponse> responses = accounts.stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update account")
    @ApiResponse(responseCode = "200", description = "Account updated successfully")
    @ApiResponse(responseCode = "404", description = "Account not found")
    public ResponseEntity<AccountResponse> updateAccount(
            @Parameter(description = "Account ID") @PathVariable Integer id,
            @Valid @RequestBody AccountRequest request) {

        log.info("REST request to update account: {}", id);

        Account account = dtoMapper.toDomain(request);
        Account updated = accountService.updateAccount(id, account);
        AccountResponse response = dtoMapper.toResponse(updated);

        return ResponseEntity.ok(response);
    }
}