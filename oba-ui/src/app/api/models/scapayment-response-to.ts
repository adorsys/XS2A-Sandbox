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

import { BearerTokenTO } from './bearer-token-to';
import { ChallengeDataTO } from './challenge-data-to';
import { ScaUserDataTO } from './sca-user-data-to';
export interface SCAPaymentResponseTO {
  paymentId?: string;
  authConfirmationCode?: string;
  bearerToken?: BearerTokenTO;
  challengeData?: ChallengeDataTO;
  chosenScaMethod?: ScaUserDataTO;
  expiresInSeconds?: number;
  multilevelScaRequired?: boolean;
  objectType?: string;
  authorisationId?: string;
  paymentProduct?: string;
  paymentType?: 'SINGLE' | 'PERIODIC' | 'BULK';
  psuMessage?: string;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?:
    | 'received'
    | 'psuIdentified'
    | 'psuAuthenticated'
    | 'scaMethodSelected'
    | 'started'
    | 'finalised'
    | 'failed'
    | 'exempted'
    | 'unconfirmed';
  statusDate?: string;
  transactionStatus?:
    | 'ACCC'
    | 'ACCP'
    | 'ACSC'
    | 'ACSP'
    | 'ACTC'
    | 'ACWC'
    | 'ACWP'
    | 'RCVD'
    | 'PDNG'
    | 'RJCT'
    | 'CANC'
    | 'ACFC'
    | 'PATC';
}
