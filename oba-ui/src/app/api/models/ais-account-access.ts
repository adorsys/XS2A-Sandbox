/* tslint:disable */
import { AccountReference } from './account-reference';
export interface AisAccountAccess {
  accounts?: Array<AccountReference>;
  allPsd2?: string;
  availableAccounts?: string;
  availableAccountsWithBalances?: string;
  balances?: Array<AccountReference>;
  transactions?: Array<AccountReference>;
}
