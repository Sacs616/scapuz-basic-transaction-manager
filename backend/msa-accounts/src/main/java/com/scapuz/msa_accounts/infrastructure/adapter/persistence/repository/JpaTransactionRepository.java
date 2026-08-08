package com.scapuz.msa_accounts.infrastructure.adapter.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.TransactionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaTransactionRepository extends JpaRepository<TransactionEntity, Integer> {

    Optional<TransactionEntity> findByTransactionNumber(String transactionNumber);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.id = :accountId ORDER BY t.createdAt DESC")
    List<TransactionEntity> findByAccountId(Integer accountId);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.id = :accountId AND t.status = :status")
    List<TransactionEntity> findByAccountIdAndStatus(Integer accountId, TransactionEntity.TransactionStatus status);

    @Query("SELECT t FROM TransactionEntity t WHERE t.account.id = :accountId AND createdAt between :startDate AND :endDate")
    List<TransactionEntity> findByAccountIdAndDateRange(Integer accountId, LocalDateTime startDate,
            LocalDateTime endDate);

    @Query("SELECT t FROM TransactionEntity t WHERE t.createdAt between :startDate AND :endDate")
    List<TransactionEntity> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    boolean existsByTransactionNumber(String transactionNumber);
}