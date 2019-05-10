/* tslint:disable */
import { AccountReferenceTO } from './account-reference-to';
import { TppInfo } from './tpp-info';

/**
 * Piis consent request
 */
export interface PiisConsentRequest {

  /**
   * Accounts for which the consent is created
   */
  accounts?: Array<AccountReferenceTO>;

  /**
   * Maximum frequency for an access per day. For a once-off access, this attribute is set to 1
   */
  allowedFrequencyPerDay: number;

  /**
   * Tpp for which the consent will be created. If the property is omitted, the consent will be created for all TPPs
   */
  tppInfo?: TppInfo;

  /**
   * Consent`s expiration date. The content is the local ASPSP date in ISODate Format
   */
  validUntil?: string;
}
