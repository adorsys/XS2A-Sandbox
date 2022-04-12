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

import { AisAccountAccessInfo } from './ais-account-access-info';

/**
 * Ais consent request
 */
export interface AisConsentRequest {
  /**
   * Set of accesses given by psu for this account
   */
  access: AisAccountAccessInfo;

  /**
   * Maximum frequency for an access per day. For a once-off access, this attribute is set to 1
   */
  frequencyPerDay: number;

  /**
   * The consent id
   */
  id: string;

  /**
   * 'true', if the consent is for recurring access to the account data , 'false', if the consent is for one access to the account data
   */
  recurringIndicator: boolean;

  /**
   * ID of the corresponding TPP.
   */
  tppId: string;

  /**
   * Corresponding PSU
   */
  userId: string;

  /**
   * Consent`s expiration date. The content is the local ASPSP date in ISODate Format
   */
  validUntil: string;
}
