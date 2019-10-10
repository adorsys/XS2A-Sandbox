/* tslint:disable */
import { AccountReference } from './account-reference';

/**
 * Piis consent request
 */
export interface PiisConsentRequest {

  /**
   * Account, where the confirmation of funds service is aimed to be submitted to.
   */
  account?: AccountReference;

  /**
   * Expiry date of the card issued by the PIISP
   */
  cardExpiryDate?: string;

  /**
   * Additional explanation for the card product.
   */
  cardInformation?: string;

  /**
   * Card Number of the card issued by the PIISP. Should be delivered if available.
   */
  cardNumber?: string;

  /**
   * Additional information about the registration process for the PSU, e.g. a reference to the TPP / PSU contract.
   */
  registrationInformation?: string;

  /**
   * Tpp attribute that fully described Tpp for which the consent will be created. If the property is omitted, the consent will be created for all TPPs
   */
  tppAuthorisationNumber?: string;

  /**
   * Consent`s expiration date. The content is the local ASPSP date in ISODate Format
   */
  validUntil?: string;
}
