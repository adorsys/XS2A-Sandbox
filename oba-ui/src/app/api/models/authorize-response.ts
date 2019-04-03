/* tslint:disable */
import { PsuMessage } from './psu-message';
import { ScaUserDataTO } from './sca-user-data-to';
export interface AuthorizeResponse {
  authorisationId?: string;
  encryptedConsentId?: string;
  psuMessages?: Array<PsuMessage>;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'partiallyAuthorised';
}
