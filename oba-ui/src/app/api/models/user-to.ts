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

import { AccountAccessTO } from './account-access-to';
import { ScaUserDataTO } from './sca-user-data-to';
export interface UserTO {
  accountAccesses?: Array<AccountAccessTO>;
  branch?: string;
  email?: string;
  id?: string;
  login?: string;
  pin?: string;
  scaUserData?: Array<ScaUserDataTO>;
  userRoles?: Array<'CUSTOMER' | 'STAFF' | 'TECHNICAL' | 'SYSTEM'>;
}
