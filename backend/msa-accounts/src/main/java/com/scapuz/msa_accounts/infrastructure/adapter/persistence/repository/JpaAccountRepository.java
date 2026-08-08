package com.scapuz.msa_accounts.infrastructure.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.AccountEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaAccountRepository extends JpaRepository<AccountEntity, Integer> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findByClientId(UUID clientId);

    List<AccountEntity> findByStatus(AccountEntity.AccountStatus status);

    boolean existsByAccountNumber(String accountNumber);

    @Query("SELECT a FROM AccountEntity a WHERE a.clientId = :clientId AND a.status = 'ACTIVE'")
    List<AccountEntity> findActiveAccountsByClientId(UUID clientId);
}