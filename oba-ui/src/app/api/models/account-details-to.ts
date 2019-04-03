/* tslint:disable */
import { AccountBalanceTO } from './account-balance-to';
export interface AccountDetailsTO {
  accountStatus?: 'ENABLED' | 'DELETED' | 'BLOCKED';
  accountType?: 'CACC' | 'CASH' | 'CHAR' | 'CISH' | 'COMM' | 'CPAC' | 'LLSV' | 'LOAN' | 'MGLD' | 'MOMA' | 'NREX' | 'ODFT' | 'ONDP' | 'OTHR' | 'SACC' | 'SLRY' | 'SVGS' | 'TAXE' | 'TRAN' | 'TRAS';
  balances?: Array<AccountBalanceTO>;
  bban?: string;
  bic?: string;
  currency?: string;
  details?: string;
  iban?: string;
  id?: string;
  linkedAccounts?: string;
  maskedPan?: string;
  msisdn?: string;
  name?: string;
  pan?: string;
  product?: string;
  usageType?: 'PRIV' | 'ORGA';
}
