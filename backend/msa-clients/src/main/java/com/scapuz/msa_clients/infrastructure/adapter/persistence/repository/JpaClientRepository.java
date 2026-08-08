package com.scapuz.msa_clients.infrastructure.adapter.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.scapuz.msa_clients.infrastructure.adapter.persistence.entity.ClientEntity;
import java.util.List;

@Repository
public interface JpaClientRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByIdentification(String identification);

    Optional<ClientEntity> findByClientId(UUID clientId);

    List<ClientEntity> findAll();

    boolean existsByIdentification(String identification);
}
