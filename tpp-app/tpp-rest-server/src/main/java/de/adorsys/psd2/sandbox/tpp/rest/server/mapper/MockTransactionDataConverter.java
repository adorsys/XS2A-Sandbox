package de.adorsys.psd2.sandbox.tpp.rest.server.mapper;

import de.adorsys.ledgers.middleware.api.domain.account.MockBookingDetails;
import de.adorsys.psd2.sandbox.tpp.rest.api.domain.UserTransaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MockTransactionDataConverter {

    List<MockBookingDetails> toLedgersMockTransactions(List<UserTransaction> transactions);

    @Mapping(source = "id", target = "userAccount")
    @Mapping(qualifiedByName = "toLocalDate", source = "postingDate", target = "bookingDate")
    @Mapping(qualifiedByName = "toLocalDate", source = "valueDate", target = "valueDate")
    @Mapping(source = "usage", target = "remittance")
    @Mapping(source = "payer", target = "crDrName")
    @Mapping(source = "accountNumber", target = "otherAccount")
    MockBookingDetails toLedgersMockTransaction(UserTransaction transaction);

    @Named("toLocalDate")
    default LocalDate toLocalDate(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return LocalDate.parse(date, formatter);
    }
}
