package com.scapuz.msa_clients.application.usecase;

import java.util.List;
import java.util.UUID;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.exception.ClientNotFoundException;
import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.domain.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GetClientUseCase {
    private final ClientRepository clientRepository;

    @Cacheable(value = "clients", key = "#identification")
    public Client executeByIdentification(String identification) {
        log.debug("Fetching client by identification: {}", identification);
        return clientRepository.findByIdentification(identification)
                .orElseThrow(() -> new ClientNotFoundException(identification));
    }

    @Cacheable(value = "clients", key = "#clientId")
    public Client executeByClientId(UUID clientId) {
        log.debug("Fetching client by clientId: {}", clientId);
        return clientRepository.findByClientId(clientId)
                .orElseThrow(() -> new ClientNotFoundException(clientId));
    }

    public List<Client> executeAll() {
        log.debug("Fetching all clients");
        return clientRepository.findAll();
    }
}
