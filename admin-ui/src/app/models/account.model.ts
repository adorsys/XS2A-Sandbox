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

import { Amount } from './amount.model';

export class Account {
  branch: string;
  id: string;
  /** International Bank Account Number */
  iban: string;
  /** Basic Bank Account Number */
  bban: string;
  /** Primary Account Number */
  pan: string;
  maskedPan: string;
  /** Mobile Subscriber Integrated Services Digital Number */
  msisdn: string;
  /** ISO 4217 currency code */
  currency: string;
  name: string;
  product: string;
  accountType: AccountType;
  accountStatus: AccountStatus;
  blocked?: boolean;
  bic: string;
  linkedAccounts: string;
  usageType: UsageType;
  details: string;
  balances: AccountBalance[];
  creditLimit: bigint;
}

export enum AccountType {
  CACC = 'Current', // Account used to post debits and credits when no specific account has been nominated
  CASH = 'Cash Payment', // Account used for the payment of cash
  CHAR = 'Charges', // Account used for charges if different from the account for payment
  CISH = 'Cash Income', // Account used for payment of income if different from the current cash account
  COMM = 'Commission', // Account used for commission if different from the account for payment
  // Account used to post settlement debit and credit entries on behalf of a designated Clearing Participant
  CPAC = 'Clearing Participant Settlement Account',
  LLSV = 'Limited Liquidity Savings Account', // Account used for savings with special interest and withdrawal terms
  LOAN = 'Loan', // Account used for loans
  MGLD = 'Marginal Lending', // Account used for a marginal lending facility
  MOMA = 'Money Market ', // Account used for money markets if different from the cash account
  NREX = 'Non Resident External', // Account used for non-resident external
  ODFT = 'Overdraft', // Account is used for overdrafts
  ONDP = 'Overnight Deposit', // Account used for overnight deposits
  OTHR = 'Other Account', // Account not otherwise specified
  // Account used to post debit and credit entries, as a result of transactions cleared
  // and settled through a specific clearing and settlement system
  SACC = 'Settlement',
  SLRY = 'Salary', // Accounts used for salary payments
  SVGS = 'Savings', // Account used for savings
  TAXE = 'Tax', // Account used for taxes if different from the account for payment
  // A transacting account is the most basic type of bank account that you can get.
  // The main difference between transaction and cheque accounts is that you usually do not get a cheque book with
  // your transacting account and neither are you offered an overdraft facility
  TRAN = 'Transacting Account',
  TRAS = 'Cash Trading', // Account used for trading if different from the current cash account
}

export enum AccountStatus {
  ENABLED = 'ENABLED',
  DELETED = 'DELETED',
  BLOCKED = 'BLOCKED',
}

export enum UsageType {
  PRIV = 'PRIV',
  ORGA = 'ORGA',
}

class AccountBalance {
  amount: Amount;
  balanceType: BalanceType;
  lastChange: Date; // LocalDateTime;
  referenceDate: Date; // LocalDate;
  lastCommittedTransaction: string;
}

export enum BalanceType {
  CLOSING_BOOKED = 'CLOSING_BOOKED',
  EXPECTED = 'EXPECTED',
  AUTHORISED = 'AUTHORISED',
  OPENING_BOOKED = 'OPENING_BOOKED',
  INTERIM_AVAILABLE = 'INTERIM_AVAILABLE',
  FORWARD_AVAILABLE = 'FORWARD_AVAILABLE',
  NONINVOICED = 'NONINVOICED',
  AVAILABLE = 'AVAILABLE',
}

export interface AccountResponse {
  accounts: Array<Account>;
  totalElements: number;
}
