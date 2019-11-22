/* tslint:disable */
import { AdditionalInformationAccess } from './additional-information-access';
import { AccountReference } from './account-reference';
export interface AisAccountAccess {
  accountAdditionalInformationAccess?: AdditionalInformationAccess;
  accounts?: Array<AccountReference>;
  allPsd2?: string;
  availableAccounts?: string;
  availableAccountsWithBalance?: string;
  balances?: Array<AccountReference>;
  transactions?: Array<AccountReference>;
}
