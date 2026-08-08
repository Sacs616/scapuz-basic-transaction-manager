package com.scapuz.msa_clients.infrastructure.adapter.rest.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Client create/update schema")
public class ClientRequest {

    @NotBlank(message = "Identification is required")
    @Schema(description = "Client identification")
    private String identification;

    @NotBlank(message = "Name is required")
    @Schema(description = "Client name")
    private String name;

    @NotBlank(message = "Genre is required")
    @Schema(description = "Client genre")
    private String genre;

    @Past(message = "Birth date must be on the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(description = "Client's birth date", example = "1990-05-15")
    private LocalDate birthDate;

    @NotBlank(message = "address is required")
    @Schema(description = "Client address")
    private String address;

    @NotBlank(message = "Phone is required")
    @Schema(description = "Client phone")
    private String phone;

    @Schema(description = "Client status", example = "ACTIVE")
    private String status;

}
