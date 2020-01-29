/* tslint:disable */
import { AisConsentRequest } from './ais-consent-request';

/**
 * The access token object.
 */
export interface AccessTokenTO {

  /**
   * The token identifier
   */
  jti?: string;

  /**
   * The bearer this token.
   */
  act?: {[key: string]: string};

  /**
   * The specification of psd2 account access permission associated with this token
   */
  consent?: AisConsentRequest;

  /**
   * expiration time
   */
  exp?: string;

  /**
   * Issue time
   */
  iat?: string;

  /**
   * The last authorisation id leading to this token
   */
  authorisation_id?: string;

  /**
   * The login name of the initiator of this token
   */
  login?: string;

  /**
   * Role to be inforced when this token is presented.
   */
  role?: 'CUSTOMER' | 'STAFF' | 'TECHNICAL' | 'SYSTEM';

  /**
   * The id of the sca object: login, payment, account access
   */
  sca_id?: string;

  /**
   * The database id of the initiator of this token
   */
  sub?: string;

  /**
   * The usage of this token.
   */
  token_usage?: 'LOGIN' | 'DIRECT_ACCESS' | 'DELEGATED_ACCESS';
}
