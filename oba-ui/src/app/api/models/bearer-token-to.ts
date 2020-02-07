/* tslint:disable */
import { AccessTokenTO } from './access-token-to';
export interface BearerTokenTO {
  accessTokenObject?: AccessTokenTO;
  access_token?: string;
  expires_in?: number;
  refresh_token?: string;
  token_type?: string;
}
