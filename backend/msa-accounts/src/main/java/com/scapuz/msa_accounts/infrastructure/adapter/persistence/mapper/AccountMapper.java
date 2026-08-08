package com.scapuz.msa_accounts.infrastructure.adapter.persistence.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.scapuz.msa_accounts.domain.model.Account;
import com.scapuz.msa_accounts.infrastructure.adapter.persistence.entity.AccountEntity;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "transactions", ignore = true)
    AccountEntity toEntity(Account account);

    @Mapping(target = "transactions", ignore = true)
    Account toDomain(AccountEntity entity);

    List<Account> toDomainList(List<AccountEntity> entities);
}