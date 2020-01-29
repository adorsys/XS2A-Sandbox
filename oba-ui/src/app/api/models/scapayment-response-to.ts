/* tslint:disable */
import { BearerTokenTO } from './bearer-token-to';
import { ChallengeDataTO } from './challenge-data-to';
import { ScaUserDataTO } from './sca-user-data-to';
export interface SCAPaymentResponseTO {
  paymentId?: string;
  authConfirmationCode?: string;
  bearerToken?: BearerTokenTO;
  challengeData?: ChallengeDataTO;
  chosenScaMethod?: ScaUserDataTO;
  expiresInSeconds?: number;
  multilevelScaRequired?: boolean;
  objectType?: string;
  authorisationId?: string;
  paymentProduct?: string;
  paymentType?: 'SINGLE' | 'PERIODIC' | 'BULK';
  psuMessage?: string;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'unconfirmed';
  statusDate?: string;
  transactionStatus?: 'ACCC' | 'ACCP' | 'ACSC' | 'ACSP' | 'ACTC' | 'ACWC' | 'ACWP' | 'RCVD' | 'PDNG' | 'RJCT' | 'CANC' | 'ACFC' | 'PATC';
}
