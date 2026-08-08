package com.scapuz.msa_clients.infrastructure.adapter.persistence.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.scapuz.msa_clients.domain.model.Client;
import com.scapuz.msa_clients.domain.repository.ClientRepository;
import com.scapuz.msa_clients.infrastructure.adapter.persistence.mapper.ClientMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientRepositoryImpl implements ClientRepository {
    private final JpaClientRepository jpaClientRepository;
    private final ClientMapper clientMapper;

    @Override
    public Client save(Client client) {
        log.debug("Saving client");
        var entity = clientMapper.toEntity(client);

        var savedEntity = jpaClientRepository.save(entity);
        return clientMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Client> findByIdentification(String identification) {
        log.debug("Finding client by identification: " + identification);
        return jpaClientRepository.findByIdentification(identification).map(clientMapper::toDomain);
    }

    @Override
    public Optional<Client> findByClientId(UUID clientId) {
        log.debug("Finding client by clientId: " + clientId);
        return jpaClientRepository.findByClientId(clientId).map(clientMapper::toDomain);

    }

    @Override
    public List<Client> findAll() {
        log.debug("Getting all clients");
        return clientMapper.toDomainList(jpaClientRepository.findAll());
    }

    @Override
    public void deleteByClientId(UUID clientId) {
        log.debug("Deleting client with id: " + clientId);
        jpaClientRepository.deleteById(clientId);
    }

    @Override
    public boolean existsByClientId(UUID clientId) {
        return jpaClientRepository.existsById(clientId);
    }

    @Override
    public boolean existsByIdentification(String identidication) {
        return jpaClientRepository.existsByIdentification(identidication);
    }
}
