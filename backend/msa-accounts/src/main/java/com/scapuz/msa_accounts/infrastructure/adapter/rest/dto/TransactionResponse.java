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
@Schema(description = "Transaction response")
public class TransactionResponse {

    @Schema(description = "Transaction ID")
    private Integer id;

    @Schema(description = "Account ID")
    private Integer accountId;

    @Schema(description = "Transaction number")
    private String transactionNumber;

    @Schema(description = "Transaction type")
    private String type;

    @Schema(description = "Amount")
    private BigDecimal amount;

    @Schema(description = "Currency code")
    private String currency;

    @Schema(description = "Transaction status")
    private String status;

    @Schema(description = "Description")
    private String description;

    @Schema(description = "Reference number")
    private String referenceNumber;

    @Schema(description = "Related account ID")
    private UUID relatedAccountId;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Creation timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Processed timestamp")
    private LocalDateTime processedAt;
}