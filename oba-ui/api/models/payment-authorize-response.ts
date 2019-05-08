/* tslint:disable */
import { BulkPaymentTO } from './bulk-payment-to';
import { PeriodicPaymentTO } from './periodic-payment-to';
import { PsuMessage } from './psu-message';
import { ScaUserDataTO } from './sca-user-data-to';
import { SinglePaymentTO } from './single-payment-to';
export interface PaymentAuthorizeResponse {
  authMessageTemplate?: string;
  authorisationId?: string;
  bulkPayment?: BulkPaymentTO;
  encryptedConsentId?: string;
  periodicPayment?: PeriodicPaymentTO;
  psuMessages?: Array<PsuMessage>;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'partiallyAuthorised';
  singlePayment?: SinglePaymentTO;
}
