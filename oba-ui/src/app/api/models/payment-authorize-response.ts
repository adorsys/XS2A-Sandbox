/* tslint:disable */
import { PaymentTO } from './payment-to';
import { PsuMessage } from './psu-message';
import { ScaUserDataTO } from './sca-user-data-to';
export interface PaymentAuthorizeResponse {
  authConfirmationCode?: string;
  authMessageTemplate?: string;
  authorisationId?: string;
  encryptedConsentId?: string;
  payment?: PaymentTO;
  psuMessages?: Array<PsuMessage>;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'unconfirmed';
}
