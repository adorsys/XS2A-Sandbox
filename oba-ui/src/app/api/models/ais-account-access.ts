/* tslint:disable */
import { AccountReference } from './account-reference';
export interface AisAccountAccess {
  accounts?: Array<AccountReference>;
  allPsd2?: string;
  availableAccounts?: string;
  availableAccountsWithBalance?: string;
  balances?: Array<AccountReference>;
  transactions?: Array<AccountReference>;
}
