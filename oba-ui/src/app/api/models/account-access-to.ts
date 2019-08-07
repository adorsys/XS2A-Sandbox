/* tslint:disable */
export interface AccountAccessTO {
  accessType?: 'OWNER' | 'READ' | 'DISPOSE';
  iban?: string;
  id?: string;
  scaWeight?: number;
}
