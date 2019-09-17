/* tslint:disable */
import { TppRedirectUri } from './tpp-redirect-uri';
export interface AuthorisationTemplate {

  /**
   * Cancel TPP redirect URIs
   */
  cancelTppRedirectUri?: TppRedirectUri;

  /**
   * TPP redirect URIs
   */
  tppRedirectUri?: TppRedirectUri;
}
