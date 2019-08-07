/* tslint:disable */
import { AccountAccessTO } from './account-access-to';
import { ScaUserDataTO } from './sca-user-data-to';
export interface UserTO {
  accountAccesses?: Array<AccountAccessTO>;
  branch?: string;
  email?: string;
  id?: string;
  login?: string;
  pin?: string;
  scaUserData?: Array<ScaUserDataTO>;
  userRoles?: Array<'CUSTOMER' | 'STAFF' | 'TECHNICAL' | 'SYSTEM'>;
}
