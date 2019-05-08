/* tslint:disable */
import { AisConsentRequest } from './ais-consent-request';
import { PsuMessage } from './psu-message';
export interface PIISConsentCreateResponse {
  consent?: AisConsentRequest;
  psuMessages?: Array<PsuMessage>;
}
