import { Account } from './account.model';
import { UserAccess } from './user-access';

export interface AccountReport {
  details: Account;
  accesses: UserAccess[];
  multilevelScaEnabled: boolean;
}
