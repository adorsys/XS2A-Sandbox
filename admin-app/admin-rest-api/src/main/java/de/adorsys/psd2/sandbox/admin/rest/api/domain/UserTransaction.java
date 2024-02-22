/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at sales@adorsys.com.
 */

package de.adorsys.psd2.sandbox.admin.rest.api.domain;

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
