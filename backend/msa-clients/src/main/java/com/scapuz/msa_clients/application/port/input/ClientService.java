package com.scapuz.msa_clients.application.port.input;

import java.util.List;
import java.util.UUID;

import com.scapuz.msa_clients.domain.model.Client;

public interface ClientService {
    Client createClient(Client client);

    Client getClientByIdentification(String identification);

    Client getClientById(UUID clientId);

    List<Client> getAllClients();

    Client updateClient(UUID clientId, Client client);

    void deleteClient(UUID clientId);
}