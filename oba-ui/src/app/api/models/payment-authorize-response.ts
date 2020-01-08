/* tslint:disable */
import {PsuMessage} from './psu-message';
import {ScaUserDataTO} from './sca-user-data-to';

export interface PaymentAuthorizeResponse {
  authMessageTemplate?: string;
  authorisationId?: string;
  encryptedConsentId?: string;
  psuMessages?: Array<PsuMessage>;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted';
  payment?: PaymentTO;
  requestedExecutionDate?: string
}

export interface PaymentTO {
  paymentId: string,
  paymentType: 'SINGLE' | 'BULK' | 'PERIODIC',
  targets?: Array<Target>
  startDate?: string,
  endDate?: string,
  executionRule?: string,
  dayOfExecution?: number,
  debtorAccount?: PaymentAccount,
}

export interface Target {
  instructedAmount?: {
    currency?: string,
    amount?: number
  },
  creditorAccount?: PaymentAccount,
  creditorAgent?: string,
  creditorName?: string,
  remittanceInformationUnstructured?: string
}

export interface PaymentAccount {
  iban?: string,
  currency?: string
}
