package com.scapuz.msa_accounts.infrastructure.adapter.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create/update account")
public class AccountRequest {

    @NotNull(message = "Client ID is required")
    @Schema(description = "Client ID", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID clientId;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^ACC[0-9]{10}$", message = "Account number must follow format ACC0000000000")
    @Schema(description = "Account number", example = "ACC0000000001")
    private String accountNumber;

    @NotBlank(message = "Account type is required")
    @Schema(description = "Account type", example = "SAVINGS")
    private String type;

    @NotNull(message = "Initial balance is required")
    @DecimalMin(value = "0.0", message = "Balance must be non-negative")
    @Schema(description = "Initial balance", example = "1000.00")
    private BigDecimal balance;

    @DecimalMin(value = "0.0", message = "Overdraft limit must be non-negative")
    @Schema(description = "Overdraft limit", example = "500.00")
    private BigDecimal overdraftLimit;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code")
    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Schema(description = "Account status", example = "ACTIVE")
    private String status;
}