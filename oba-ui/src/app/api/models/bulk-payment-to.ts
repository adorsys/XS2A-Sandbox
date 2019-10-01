/* tslint:disable */
import { AccountReferenceTO } from './account-reference-to';
import { SinglePaymentTO } from './single-payment-to';

export interface BulkPaymentTO {
  batchBookingPreferred?: boolean;
  debtorAccount?: AccountReferenceTO;
  paymentId?: string;
  paymentProduct?: 'SEPA' | 'INSTANT_SEPA' | 'TARGET2' | 'CROSS_BORDER';
  paymentStatus?: 'ACCC' | 'ACCP' | 'ACSC' | 'ACSP' | 'ACTC' | 'ACWC' | 'ACWP' | 'RCVD' | 'PDNG' | 'RJCT' | 'CANC' | 'ACFC' | 'PATC';
  payments?: Array<SinglePaymentTO>;
  requestedExecutionDate?: string;
}
