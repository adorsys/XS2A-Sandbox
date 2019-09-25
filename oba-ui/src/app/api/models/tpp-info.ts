/* tslint:disable */
import { TppRedirectUri } from './tpp-redirect-uri';
export interface TppInfo {

  /**
   * Authorization number
   */
  authorisationNumber: string;

  /**
   * National competent authority id
   */
  authorityId: string;

  /**
   * National competent authority name
   */
  authorityName: string;

  /**
   * Cancel TPP redirect URIs
   */
  cancelTppRedirectUri?: TppRedirectUri;

  /**
   * City
   */
  city: string;

  /**
   * Country
   */
  country: string;

  /**
   * Issuer CN
   */
  issuerCN: string;

  /**
   * Organisation
   */
  organisation: string;

  /**
   * Organisation unit
   */
  organisationUnit: string;

  /**
   * State
   */
  state: string;

  /**
   * Tpp name
   */
  tppName: string;

  /**
   * TPP redirect URIs
   */
  tppRedirectUri?: TppRedirectUri;

  /**
   * Tpp role
   */
  tppRoles: Array<'PISP' | 'AISP' | 'PIISP' | 'ASPSP'>;
}
