/* tslint:disable */
import { PsuIdData } from './psu-id-data';
export interface AisAccountConsentAuthorisation {
  id?: string;
  psuIdData?: PsuIdData;
  scaStatus?: 'received' | 'psuIdentified' | 'psuAuthenticated' | 'scaMethodSelected' | 'started' | 'finalised' | 'failed' | 'exempted' | 'unconfirmed';
}
