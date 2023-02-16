/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
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

import { AisAccountAccess } from './ais-account-access';
import { AuthorisationTemplate } from './authorisation-template';
import { AisAccountConsentAuthorisation } from './ais-account-consent-authorisation';
import { PsuIdData } from './psu-id-data';
import { TppInfo } from './tpp-info';
export interface CmsAisAccountConsent {
  multilevelScaRequired?: boolean;
  access?: AisAccountAccess;
  aisConsentRequestType?:
    | 'GLOBAL'
    | 'ALL_AVAILABLE_ACCOUNTS'
    | 'BANK_OFFERED'
    | 'DEDICATED_ACCOUNTS';
  authorisationTemplate?: AuthorisationTemplate;
  consentStatus?:
    | 'RECEIVED'
    | 'REJECTED'
    | 'VALID'
    | 'REVOKED_BY_PSU'
    | 'EXPIRED'
    | 'TERMINATED_BY_TPP'
    | 'TERMINATED_BY_ASPSP'
    | 'PARTIALLY_AUTHORISED';
  creationTimestamp?: string;
  frequencyPerDay?: number;
  id?: string;
  lastActionDate?: string;
  accountConsentAuthorizations?: Array<AisAccountConsentAuthorisation>;
  psuIdDataList?: Array<PsuIdData>;
  recurringIndicator?: boolean;
  statusChangeTimestamp?: string;
  tppInfo?: TppInfo;
  tppRedirectPreferred?: boolean;
  usageCounterMap?: { [key: string]: number };
  validUntil?: string;
  withBalance?: boolean;
}
