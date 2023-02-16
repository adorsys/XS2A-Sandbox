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

import { Account } from './account.model';

export class ExtendedBalance {
  isCreditEnabled: boolean;
  limit?: string;
  personal?: string;
  creditLeft?: string;
  balance?: string;

  constructor(details: Account) {
    if (details && details.balances && details.balances.length > 1) {
      const balance = details.balances[0]?.amount?.amount;
      const currency = details.balances[0]?.amount?.currency;
      const limit = Number(details?.creditLimit);

      this.isCreditEnabled = details.creditLimit > 0;
      this.limit = `${limit} ${currency}`;
      this.balance = `${this.isCreditEnabled ? balance + limit : balance} ${currency}`;

      if (this.isCreditEnabled) {
        this.personal = `${balance < 0 ? 0 : balance} ${currency}`;
        this.creditLeft = `${balance < 0 ? limit + balance : limit} ${currency}`;
      }
    }
  }
}
