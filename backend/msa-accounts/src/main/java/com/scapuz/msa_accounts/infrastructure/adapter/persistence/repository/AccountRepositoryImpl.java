package com.scapuz.msa_accounts.infrastructure.adapter.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.domain.repository.AccountRepository;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.AccountEntity;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.mapper.AccountMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountRepositoryImpl implements AccountRepository {

    private final JpaAccountRepository jpaRepository;
    private final AccountMapper mapper;

    @Override
    public Account save(Account account) {
        log.debug("Saving account to database");
        var entity = mapper.toEntity(account);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Account> findById(Integer id) {
        log.debug("Finding account by id: {}", id);
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        log.debug("Finding account by number: {}", accountNumber);
        return jpaRepository.findByAccountNumber(accountNumber)
                .map(mapper::toDomain);
    }

    @Override
    public List<Account> findByClientId(UUID clientId) {
        log.debug("Finding accounts by client id: {}", clientId);
        return mapper.toDomainList(jpaRepository.findByClientId(clientId));
    }

    @Override
    public List<Account> findByStatus(Account.AccountStatus status) {
        log.debug("Finding accounts by status: {}", status);
        var entityStatus = AccountEntity.AccountStatus.valueOf(status.name());
        return mapper.toDomainList(jpaRepository.findByStatus(entityStatus));
    }

    @Override
    public void deleteById(Integer id) {
        log.debug("Deleting account by id: {}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByAccountNumber(String accountNumber) {
        return jpaRepository.existsByAccountNumber(accountNumber);
    }
}