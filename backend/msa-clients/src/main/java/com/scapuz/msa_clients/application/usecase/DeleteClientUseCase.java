package com.scapuz.msa_clients.application.usecase;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.exception.ClientNotFoundException;
import com.scapuz.msa_clients.domain.repository.ClientRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteClientUseCase {
    private final ClientRepository clientRepository;

    public void execute(UUID clientId) {
        log.info("Deleting client: " + clientId);
        if (!clientRepository.findByClientId(clientId).isPresent()) {
            throw new ClientNotFoundException(clientId);
        }

        clientRepository.deleteByClientId(clientId);
        log.info("Client deleted: " + clientId);
    }
}
