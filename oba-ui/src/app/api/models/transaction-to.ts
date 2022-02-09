/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
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
 * contact us at psd2@adorsys.com.
 */

/* tslint:disable */
import { AmountTO } from './amount-to';
import { AccountReferenceTO } from './account-reference-to';
import { ExchangeRateTO } from './exchange-rate-to';
import { RemittanceInformationStructuredTO } from './remittance-information-structured-to';
export interface TransactionTO {
  entryReference?: string;
  amount?: AmountTO;
  bookingDate?: string;
  checkId?: string;
  creditorAccount?: AccountReferenceTO;
  creditorId?: string;
  creditorName?: string;
  debtorAccount?: AccountReferenceTO;
  debtorName?: string;
  endToEndId?: string;
  bankTransactionCode?: string;
  exchangeRate?: Array<ExchangeRateTO>;
  mandateId?: string;
  proprietaryBankTransactionCode?: string;
  purposeCode?: string;
  remittanceInformationStructured?: RemittanceInformationStructuredTO;
  remittanceInformationUnstructured?: string;
  transactionId?: string;
  ultimateCreditor?: string;
  ultimateDebtor?: string;
  valueDate?: string;
}
