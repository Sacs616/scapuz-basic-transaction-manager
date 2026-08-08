package com.scapuz.msa_clients.infrastructure.adapter.rest.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client response")
public class ClientResponse {

    @Schema(description = "Client unique identifier")
    private UUID clientId;

    @Schema(description = "Client identification")
    private String identification;

    @Schema(description = "Client's name")
    private String name;

    @Schema(description = "Client genre")
    private String genre;

    @Schema(description = "Client's address")
    private String address;

    @Schema(description = "Client's phone number")
    private String phone;

    @Schema(description = "Client's age")
    private Integer age;

    @Schema(description = "Client status")
    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Registration timestamp")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Last modification timestamp")
    private LocalDateTime updatedAt;
}
