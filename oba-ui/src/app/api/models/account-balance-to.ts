/* tslint:disable */
import { AmountTO } from './amount-to';
export interface AccountBalanceTO {
  amount?: AmountTO;
  balanceType?: 'CLOSING_BOOKED' | 'EXPECTED' | 'AUTHORISED' | 'OPENING_BOOKED' | 'INTERIM_AVAILABLE' | 'FORWARD_AVAILABLE' | 'NONINVOICED';
  iban?: string;
  lastChangeDateTime?: string;
  lastCommittedTransaction?: string;
  referenceDate?: string;
}
