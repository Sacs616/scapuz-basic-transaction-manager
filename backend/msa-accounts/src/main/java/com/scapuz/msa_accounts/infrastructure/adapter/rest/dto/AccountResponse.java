package com.scapuz.msa_accounts.infrastructure.adapter.rest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Account response")
public class AccountResponse {

    @Schema(description = "Account ID")
    private Integer id;

    @Schema(description = "Client ID")
    private UUID clientId;

    @Schema(description = "Account number")
    private String accountNumber;

    @Schema(description = "Account type")
    private String type;

    @Schema(description = "Account status")
    private String status;

    @Schema(description = "Current balance")
    private BigDecimal balance;

    @Schema(description = "Available balance (including overdraft)")
    private BigDecimal availableBalance;

    @Schema(description = "Overdraft limit")
    private BigDecimal overdraftLimit;

    @Schema(description = "Currency code")
    private String currency;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last update timestamp")
    private LocalDateTime updatedAt;
}
