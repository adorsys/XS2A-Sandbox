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

import { AccountReferenceTO } from './account-reference-to';
import { LocalTime } from './local-time';
import { PaymentTargetTO } from './payment-target-to';
export interface PaymentTO {
  frequency?:
    | 'Daily'
    | 'Weekly'
    | 'EveryTwoWeeks'
    | 'Monthly'
    | 'EveryTwoMonths'
    | 'Quarterly'
    | 'SemiAnnual'
    | 'Annual'
    | 'Monthlyvariable';
  accountId?: string;
  dayOfExecution?: number;
  debtorAccount?: AccountReferenceTO;
  debtorAgent?: string;
  debtorName?: string;
  endDate?: string;
  executionRule?: string;
  batchBookingPreferred?: boolean;
  paymentId?: string;
  paymentProduct?: string;
  paymentType?: 'SINGLE' | 'PERIODIC' | 'BULK';
  requestedExecutionDate?: string;
  requestedExecutionTime?: LocalTime;
  startDate?: string;
  targets?: Array<PaymentTargetTO>;
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
