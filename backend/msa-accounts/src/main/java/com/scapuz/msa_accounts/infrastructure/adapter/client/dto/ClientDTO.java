package com.scapuz.msa_accounts.infrastructure.adapter.client.dto;

import java.io.Serializable;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO implements Serializable {
    private UUID clientId;
    private String name;
    private String phone;
    private Integer age;
    private String status;
}
