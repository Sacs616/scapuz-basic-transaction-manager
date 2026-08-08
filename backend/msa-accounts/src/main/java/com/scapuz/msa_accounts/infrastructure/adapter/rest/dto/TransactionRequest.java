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
@Schema(description = "Request to create transaction")
public class TransactionRequest {

    @NotNull(message = "Account ID is required")
    @Schema(description = "Account ID")
    private Integer accountId;

    @NotBlank(message = "Transaction number is required")
    @Pattern(regexp = "^TXN\\d{12}$", message = "Transaction number must follow format TXN000000000000")
    @Schema(description = "Transaction number", example = "TXN000000000001")
    private String transactionNumber;

    @NotBlank(message = "Transaction type is required")
    @Schema(description = "Transaction type", example = "DEPOSIT")
    private String type;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be positive")
    @Schema(description = "Transaction amount", example = "100.00")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Currency must be 3-letter ISO code")
    @Schema(description = "Currency code", example = "USD")
    private String currency;

    @Size(max = 500, message = "Description must not exceed 500 characters")
    @Schema(description = "Transaction description", example = "ATM deposit")
    private String description;

    @Schema(description = "Reference number", example = "REF123456")
    private String referenceNumber;

    @Schema(description = "Related account ID for transfers")
    private UUID relatedAccountId;
}