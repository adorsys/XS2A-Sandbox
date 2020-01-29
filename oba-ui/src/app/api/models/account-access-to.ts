/* tslint:disable */
export interface AccountAccessTO {
  accessType?: 'OWNER' | 'READ' | 'DISPOSE';
  accountId?: string;
  currency?: string;
  iban?: string;
  id?: string;
  scaWeight?: number;
}
