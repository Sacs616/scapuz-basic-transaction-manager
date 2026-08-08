package com.scapuz.msa_clients.application.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scapuz.msa_clients.application.port.input.ClientService;
import com.scapuz.msa_clients.application.usecase.*;
import com.scapuz.msa_clients.domain.model.Client;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientServiceImpl implements ClientService {
    private final CreateClientUseCase createClientUseCase;
    private final GetClientUseCase getClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final DeleteClientUseCase deleteClientUseCase;

    @Override
    public Client createClient(Client client) {
        return createClientUseCase.execute(client);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientByIdentification(String identification) {
        return getClientUseCase.executeByIdentification(identification);
    }

    @Override
    @Transactional(readOnly = true)
    public Client getClientById(UUID clientId) {
        return getClientUseCase.executeByClientId(clientId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Client> getAllClients() {
        return getClientUseCase.executeAll();
    }

    @Override
    public Client updateClient(UUID clientId, Client client) {
        return updateClientUseCase.execute(clientId, client);
    }

    @Override
    public void deleteClient(UUID clientId) {
        deleteClientUseCase.execute(clientId);
    }
}
