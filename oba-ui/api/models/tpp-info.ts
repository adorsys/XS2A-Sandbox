/* tslint:disable */
import { TppRedirectUri } from './tpp-redirect-uri';
export interface TppInfo {

  /**
   * Organisation
   */
  organisation: string;

  /**
   * Authorization number
   */
  authorisationNumber: string;

  /**
   * National competent authority name
   */
  authorityName: string;

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
   * National competent authority id
   */
  authorityId: string;

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
