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
import { AccountReference } from './account-reference';

/**
 * Piis consent request
 */
export interface PiisConsentRequest {
  /**
   * Account, where the confirmation of funds service is aimed to be submitted to.
   */
  account?: AccountReference;

  /**
   * Expiry date of the card issued by the PIISP
   */
  cardExpiryDate?: string;

  /**
   * Additional explanation for the card product.
   */
  cardInformation?: string;

  /**
   * Card Number of the card issued by the PIISP. Should be delivered if available.
   */
  cardNumber?: string;

  /**
   * Additional information about the registration process for the PSU, e.g. a reference to the TPP / PSU contract.
   */
  registrationInformation?: string;

  /**
   * Tpp attribute that fully described Tpp for which the consent will be created. If the property is omitted, the consent will be created for all TPPs
   */
  tppAuthorisationNumber?: string;

  /**
   * Consent`s expiration date. The content is the local ASPSP date in ISODate Format
   */
  validUntil?: string;
}
