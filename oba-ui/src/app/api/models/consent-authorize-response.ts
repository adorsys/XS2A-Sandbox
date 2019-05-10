/* tslint:disable */
import { AccountDetailsTO } from './account-details-to';
import { AisConsentRequest } from './ais-consent-request';
import { PsuMessage } from './psu-message';
import { ScaUserDataTO } from './sca-user-data-to';
export interface ConsentAuthorizeResponse {
  accounts?: Array<AccountDetailsTO>;
  authMessageTemplate?: string;
  authorisationId?: string;
  consent?: AisConsentRequest;
  encryptedConsentId?: string;
  psuMessages?: Array<PsuMessage>;
  scaMethods?: Array<ScaUserDataTO>;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'partiallyAuthorised';
}
