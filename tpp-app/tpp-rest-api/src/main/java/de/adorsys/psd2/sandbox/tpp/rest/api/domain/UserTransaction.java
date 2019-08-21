package de.adorsys.psd2.sandbox.tpp.rest.api.domain;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class UserTransaction {
    @CsvBindByName(column = "Auftragskonto", required = true)
    private String id;

    @CsvBindByName(column = "Buchungstag", required = true)
    private String postingDate;

    @CsvBindByName(column = "Valutadatum", required = true)
    private String valueDate;

    @CsvBindByName(column = "Buchungstext", required = true)
    private String text;

    @CsvBindByName(column = "Verwendungszweck", required = true)
    private String usage;

    @CsvBindByName(column = "Beguenstigter/Zahlungspflichtiger", required = true)
    private String payer;

    @CsvBindByName(column = "Kontonummer", required = true)
    private String accountNumber;

    @CsvBindByName(column = "BLZ", required = true)
    private String blzCode;

    @CsvBindByName(column = "Betrag", required = true)
    private String amount;

    @CsvBindByName(column = "Waehrung", required = true)
    private String currency;
}
