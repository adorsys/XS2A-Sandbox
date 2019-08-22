/* tslint:disable */
import { AisAccountAccess } from './ais-account-access';
import { AisAccountConsentAuthorisation } from './ais-account-consent-authorisation';
import { PsuIdData } from './psu-id-data';
import { TppInfo } from './tpp-info';
export interface AisAccountConsent {
  access?: AisAccountAccess;
  accountConsentAuthorizations?: Array<AisAccountConsentAuthorisation>;
  aisConsentRequestType?: 'GLOBAL' | 'ALL_AVAILABLE_ACCOUNTS' | 'BANK_OFFERED' | 'DEDICATED_ACCOUNTS';
  consentStatus?: 'RECEIVED' | 'REJECTED' | 'VALID' | 'REVOKED_BY_PSU' | 'EXPIRED' | 'TERMINATED_BY_TPP' | 'TERMINATED_BY_ASPSP' | 'PARTIALLY_AUTHORISED';
  creationTimestamp?: string;
  frequencyPerDay?: number;
  id?: string;
  lastActionDate?: string;
  multilevelScaRequired?: boolean;
  psuIdDataList?: Array<PsuIdData>;
  recurringIndicator?: boolean;
  statusChangeTimestamp?: string;
  tppInfo?: TppInfo;
  tppRedirectPreferred?: boolean;
  usageCounterMap?: {[key: string]: number};
  validUntil?: string;
  withBalance?: boolean;
}
