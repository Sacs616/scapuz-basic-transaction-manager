package com.scapuz.msa_accounts.infrastructure.adapter.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.scapuz.msa_accounts.domain.model.Transaction;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.TransactionEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    @Mapping(target = "account", ignore = true)
    @Mapping(source = "accountId", target = "account.id")
    TransactionEntity toEntity(Transaction transaction);

    @Mapping(source = "account.id", target = "accountId")
    Transaction toDomain(TransactionEntity entity);

    List<Transaction> toDomainList(List<TransactionEntity> entities);
}