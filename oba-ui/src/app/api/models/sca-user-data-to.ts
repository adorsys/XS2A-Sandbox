/* tslint:disable */
import { UserTO } from './user-to';
export interface ScaUserDataTO {
  id?: string;
  methodValue?: string;
  scaMethod?: 'EMAIL' | 'MOBILE';
  staticTan?: string;
  user?: UserTO;
  usesStaticTan?: boolean;
}
