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

/**
 * Account Reference
 */
export interface AccountReference {
  aspspAccountId?: string;

  /**
   * BBAN: This data elements is used for payment accounts which have no IBAN
   */
  bban?: string;

  /**
   * Codes following ISO 4217
   */
  currency?: string;

  /**
   * IBAN: This data element can be used in the body of the CreateConsentReq Request Message for retrieving account access consent from this payment account
   */
  iban?: string;

  /**
   * MASKEDPAN: Primary Account Number (PAN) of a card in a masked form.
   */
  maskedPan?: string;

  /**
   * MSISDN: An alias to access a payment account via a registered mobile phone number. This alias might be needed e.g. in the payment initiation service, cp. Section 5.3.1. The support of this alias must be explicitly documented by the ASPSP for the corresponding API calls.
   */
  msisdn?: string;

  /**
   * PAN: Primary Account Number (PAN) of a card, can be tokenized by the ASPSP due to PCI DSS requirements.
   */
  pan?: string;

  /**
   * RESOURCE-ID: This identification is denoting the addressed account.
   */
  resourceId?: string;
}
