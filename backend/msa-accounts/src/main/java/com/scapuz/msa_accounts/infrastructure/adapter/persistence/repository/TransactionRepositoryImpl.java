package com.scapuz.msa_accounts.infrastructure.adapter.persistence.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.domain.repository.TransactionRepository;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.TransactionEntity;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.mapper.TransactionMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {

    private final JpaTransactionRepository jpaRepository;
    private final TransactionMapper mapper;

    @Override
    public Transaction save(Transaction transaction) {
        log.debug("Saving transaction to database");
        var entity = mapper.toEntity(transaction);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(Integer id) {
        log.debug("Finding transaction by id: {}", id);
        return jpaRepository.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Transaction> findByTransactionNumber(String transactionNumber) {
        log.debug("Finding transaction by number: {}", transactionNumber);
        return jpaRepository.findByTransactionNumber(transactionNumber)
                .map(mapper::toDomain);
    }

    @Override
    public List<Transaction> findByAccountId(Integer accountId) {
        log.debug("Finding transactions by account id: {}", accountId);
        return mapper.toDomainList(jpaRepository.findByAccountId(accountId));
    }

    @Override
    public List<Transaction> findByAccountIdAndStatus(Integer accountId, Transaction.TransactionStatus status) {
        log.debug("Finding transactions by account id and status");
        var entityStatus = TransactionEntity.TransactionStatus.valueOf(status.name());
        return mapper.toDomainList(
                jpaRepository.findByAccountIdAndStatus(accountId, entityStatus));
    }

    @Override
    public boolean existsByTransactionNumber(String transactionNumber) {
        return jpaRepository.existsByTransactionNumber(transactionNumber);
    }

    @Override
    public List<Transaction> findByAccountIdAndDateRange(Integer accountId, LocalDateTime startDate,
            LocalDateTime endDate) {
        log.debug("Finding transactions for account {} between {} and {}",
                accountId, startDate, endDate);
        return mapper.toDomainList(jpaRepository.findByAccountIdAndDateRange(accountId, startDate, endDate));
    }

    @Override
    public List<Transaction> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        log.debug("Finding all transactions between {} and {}", startDate, endDate);
        return mapper.toDomainList(jpaRepository.findByDateRange(startDate, endDate));
    }
}