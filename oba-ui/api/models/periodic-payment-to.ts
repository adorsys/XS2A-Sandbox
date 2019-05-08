/* tslint:disable */
import { AccountReferenceTO } from './account-reference-to';
import { AddressTO } from './address-to';
import { AmountTO } from './amount-to';
import { LocalTime } from './local-time';
export interface PeriodicPaymentTO {
  creditorAccount?: AccountReferenceTO;
  creditorAddress?: AddressTO;
  creditorAgent?: string;
  creditorName?: string;
  dayOfExecution?: number;
  debtorAccount?: AccountReferenceTO;
  endDate?: string;
  endToEndIdentification?: string;
  executionRule?: string;
  frequency?: 'Daily' | 'Weekly' | 'EveryTwoWeeks' | 'Monthly' | 'EveryTwoMonths' | 'Quarterly' | 'SemiAnnual' | 'Annual';
  instructedAmount?: AmountTO;
  paymentId?: string;
  paymentProduct?: 'SEPA' | 'INSTANT_SEPA' | 'TARGET2' | 'CROSS_BORDER';
  paymentStatus?: 'ACCC' | 'ACCP' | 'ACSC' | 'ACSP' | 'ACTC' | 'ACWC' | 'ACWP' | 'RCVD' | 'PDNG' | 'RJCT' | 'CANC' | 'ACFC' | 'PATC';
  remittanceInformationUnstructured?: string;
  requestedExecutionDate?: string;
  requestedExecutionTime?: LocalTime;
  startDate?: string;
}
