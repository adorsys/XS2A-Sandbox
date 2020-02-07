/* tslint:disable */
import { UserTO } from './user-to';
export interface ScaUserDataTO {
  decoupled?: boolean;
  id?: string;
  methodValue?: string;
  scaMethod?: 'EMAIL' | 'MOBILE' | 'CHIP_OTP' | 'PHOTO_OTP' | 'PUSH_OTP' | 'SMS_OTP' | 'APP_OTP';
  staticTan?: string;
  user?: UserTO;
  usesStaticTan?: boolean;
  valid?: boolean;
}
