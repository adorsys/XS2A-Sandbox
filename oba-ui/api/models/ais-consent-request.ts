/* tslint:disable */
import { AisAccountAccessInfo } from './ais-account-access-info';

/**
 * Ais consent request
 */
export interface AisConsentRequest {

  /**
   * Set of accesses given by psu for this account
   */
  access: AisAccountAccessInfo;

  /**
   * Maximum frequency for an access per day. For a once-off access, this attribute is set to 1
   */
  frequencyPerDay: number;

  /**
   * The consent id
   */
  id: string;

  /**
   * 'true', if the consent is for recurring access to the account data , 'false', if the consent is for one access to the account data
   */
  recurringIndicator: boolean;

  /**
   * ID of the corresponding TPP.
   */
  tppId: string;

  /**
   * Corresponding PSU
   */
  userId: string;

  /**
   * Consent`s expiration date. The content is the local ASPSP date in ISODate Format
   */
  validUntil: string;
}
