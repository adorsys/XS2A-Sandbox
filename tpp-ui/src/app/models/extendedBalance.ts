import {Account} from './account.model';

export class ExtendedBalance {
  isCreditEnabled: boolean;
  limit?: string;
  personal?: string;
  creditLeft?: string;
  balance?: string;

  constructor(details: Account) {
    if (details && details.balances) {
      const balance = details.balances[0].amount.amount;
      const currency = details.balances[0].amount.currency;
      const limit = Number(details.creditLimit);

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
