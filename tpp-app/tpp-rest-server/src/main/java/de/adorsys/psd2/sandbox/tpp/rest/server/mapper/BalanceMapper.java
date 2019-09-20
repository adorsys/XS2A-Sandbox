package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.AccountBalanceTO;
import de.adorsys.psd2.sandbox.tpp.rest.server.model.AccountBalance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
    @Mapping(source = "currency", target = "amount.currency")
    @Mapping(source = "amount", target = "amount.amount")
    AccountBalanceTO toAccountBalanceTO(AccountBalance source);
}
