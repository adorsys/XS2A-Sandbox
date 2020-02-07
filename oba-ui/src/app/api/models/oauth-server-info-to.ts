/* tslint:disable */
export interface OauthServerInfoTO {
  authorization_endpoint?: string;
  grant_types_supported?: Array<'AUTHORISATION_CODE' | 'REFRESH_TOKEN'>;
  response_types_supported?: Array<'CODE'>;
  token_endpoint?: string;
}
