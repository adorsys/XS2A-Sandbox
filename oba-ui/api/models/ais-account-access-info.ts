/* tslint:disable */

/**
 * Ais account access information
 */
export interface AisAccountAccessInfo {

  /**
   * Access to accounts
   */
  accounts?: Array<string>;

  /**
   * Consent on all accounts, balances and transactions of psu
   */
  allPsd2?: 'ALL_ACCOUNTS' | 'ALL_ACCOUNTS_WITH_BALANCES';

  /**
   * Consent on all available accounts of psu
   */
  availableAccounts?: 'ALL_ACCOUNTS' | 'ALL_ACCOUNTS_WITH_BALANCES';

  /**
   * Access to balances
   */
  balances?: Array<string>;

  /**
   * Access to transactions
   */
  transactions?: Array<string>;
}
