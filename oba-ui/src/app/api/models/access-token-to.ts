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

import { AisConsentRequest } from './ais-consent-request';

/**
 * The access token object.
 */
export interface AccessTokenTO {
  /**
   * The token identifier
   */
  jti?: string;

  /**
   * The bearer this token.
   */
  act?: { [key: string]: string };

  /**
   * The specification of psd2 account access permission associated with this token
   */
  consent?: AisConsentRequest;

  /**
   * expiration time
   */
  exp?: string;

  /**
   * Issue time
   */
  iat?: string;

  /**
   * The last authorisation id leading to this token
   */
  authorisation_id?: string;

  /**
   * The login name of the initiator of this token
   */
  login?: string;

  /**
   * Role to be inforced when this token is presented.
   */
  role?: 'CUSTOMER' | 'STAFF' | 'TECHNICAL' | 'SYSTEM';

  /**
   * The id of the sca object: login, payment, account access
   */
  sca_id?: string;

  /**
   * The database id of the initiator of this token
   */
  sub?: string;

  /**
   * The usage of this token.
   */
  token_usage?: 'LOGIN' | 'DIRECT_ACCESS' | 'DELEGATED_ACCESS';
}
