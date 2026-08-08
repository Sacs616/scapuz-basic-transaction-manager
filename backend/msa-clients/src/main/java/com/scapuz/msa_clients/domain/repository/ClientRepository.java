package com.scapuz.msa_clients.domain.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.scapuz.msa_clients.domain.model.Client;

public interface ClientRepository {
    Client save(Client client);

    Optional<Client> findByIdentification(String identification);

    Optional<Client> findByClientId(UUID clientId);

    List<Client> findAll();

    void deleteByClientId(UUID clientId);

    boolean existsByClientId(UUID clientId);

    boolean existsByIdentification(String identidication);

}
