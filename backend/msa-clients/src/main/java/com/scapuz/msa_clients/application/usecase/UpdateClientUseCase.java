package com.scapuz.msa_clients.application.usecase;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.exception.ClientNotFoundException;
import com.scapuz.msa_clients.domain.exception.DomainException;
import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.domain.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UpdateClientUseCase {
    private final ClientRepository clientRepository;

    @CacheEvict(value = "clients", key = "#clientId")
    public Client execute(UUID clientId, Client updatedClient) {
        log.debug("Updating client: " + clientId);
        Client existingClient = clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));

        if (!existingClient.getIdentification().equals(updatedClient.getIdentification()) &&
                clientRepository.existsByIdentification(updatedClient.getIdentification())) {
            throw new DomainException("Client identification already exists");
        }

        Client clientToSave = Client.builder()
                .clientId(clientId)
                .identification(updatedClient.getIdentification())
                .name(updatedClient.getName())
                .genre(updatedClient.getGenre())
                .birthDate(updatedClient.getBirthDate())
                .address(updatedClient.getAddress())
                .phone(updatedClient.getPhone())
                .passwordHash(updatedClient.getIdentification() + "hash + salt")
                .status(Client.ClientStatus.ACTIVE)
                .createdAt(existingClient.getCreatedAt())
                .updatedAt(LocalDateTime.now())
                .build();

        Client saved = clientRepository.save(clientToSave);
        log.info("Client updated with clientId: " + saved.getClientId());
        return saved;
    }
}
