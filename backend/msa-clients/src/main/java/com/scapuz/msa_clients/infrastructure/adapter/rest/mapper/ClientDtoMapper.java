package com.scapuz.msa_clients.infrastructure.adapter.rest.mapper;

import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientRequest;
import com.scapuz.msa_clients.infrastructure.adapter.rest.dto.ClientResponse;

@Component
public class ClientDtoMapper {

    public Client toDomain(ClientRequest request) {
        return Client.builder()
                .name(request.getName())
                .identification(request.getIdentification())
                .genre(request.getGenre())
                .birthDate(request.getBirthDate())
                .address(request.getAddress())
                .phone(request.getPhone())
                .status(request.getStatus() != null ? Client.ClientStatus.valueOf(request.getStatus())
                        : Client.ClientStatus.ACTIVE)
                .build();
    }

    public ClientResponse toResponse(Client client) {
        return ClientResponse.builder()
                .clientId(client.getClientId())
                .identification(client.getIdentification())
                .name(client.getName())
                .genre(client.getGenre())
                .age(client.getAge())
                .address(client.getAddress())
                .phone(client.getPhone())
                .status(client.getStatus().name())
                .createdAt(client.getCreatedAt())
                .updatedAt(client.getUpdatedAt())
                .build();
    }
}
