package com.scapuz.msa_clients.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.exception.DomainException;
import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.domain.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class CreateClientUseCase {
    private final ClientRepository clientRepository;

    public Client execute(Client client) {
        log.info("Creating new client for " + client.getIdentification());

        if (clientRepository.existsByIdentification(client.getIdentification())) {
            throw new DomainException("Client identification already exists");
        }

        Client newClient = Client.builder()
                .clientId(UUID.randomUUID())
                .identification(client.getIdentification())
                .name(client.getName())
                .genre(client.getGenre())
                .birthDate(client.getBirthDate())
                .address(client.getAddress())
                .phone(client.getPhone())
                .clientId(UUID.randomUUID())
                .passwordHash(client.getIdentification() + "hash + salt")
                .status(Client.ClientStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        log.info("GENRE: " + newClient.getGenre());

        return clientRepository.save(newClient);
    }
}
