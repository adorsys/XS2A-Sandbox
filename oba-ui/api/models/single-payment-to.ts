/* tslint:disable */
import { AccountReferenceTO } from './account-reference-to';
import { AddressTO } from './address-to';
import { AmountTO } from './amount-to';
import { LocalTime } from './local-time';
export interface SinglePaymentTO {
  creditorAccount?: AccountReferenceTO;
  creditorAddress?: AddressTO;
  creditorAgent?: string;
  creditorName?: string;
  debtorAccount?: AccountReferenceTO;
  endToEndIdentification?: string;
  instructedAmount?: AmountTO;
  paymentId?: string;
  paymentProduct?: 'SEPA' | 'INSTANT_SEPA' | 'TARGET2' | 'CROSS_BORDER';
  paymentStatus?: 'ACCC' | 'ACCP' | 'ACSC' | 'ACSP' | 'ACTC' | 'ACWC' | 'ACWP' | 'RCVD' | 'PDNG' | 'RJCT' | 'CANC' | 'ACFC' | 'PATC';
  remittanceInformationUnstructured?: string;
  requestedExecutionDate?: string;
  requestedExecutionTime?: LocalTime;
}
