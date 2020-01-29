/* tslint:disable */
import { AccountReferenceTO } from './account-reference-to';
import { LocalTime } from './local-time';
import { PaymentTargetTO } from './payment-target-to';
export interface PaymentTO {
  frequency?: 'Daily' | 'Weekly' | 'EveryTwoWeeks' | 'Monthly' | 'EveryTwoMonths' | 'Quarterly' | 'SemiAnnual' | 'Annual' | 'Monthlyvariable';
  accountId?: string;
  dayOfExecution?: number;
  debtorAccount?: AccountReferenceTO;
  debtorAgent?: string;
  debtorName?: string;
  endDate?: string;
  executionRule?: string;
  batchBookingPreferred?: boolean;
  paymentId?: string;
  paymentProduct?: string;
  paymentType?: 'SINGLE' | 'PERIODIC' | 'BULK';
  requestedExecutionDate?: string;
  requestedExecutionTime?: LocalTime;
  startDate?: string;
  targets?: Array<PaymentTargetTO>;
  transactionStatus?: 'ACCC' | 'ACCP' | 'ACSC' | 'ACSP' | 'ACTC' | 'ACWC' | 'ACWP' | 'RCVD' | 'PDNG' | 'RJCT' | 'CANC' | 'ACFC' | 'PATC';
}
