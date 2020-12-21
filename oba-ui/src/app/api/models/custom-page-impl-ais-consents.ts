/* tslint:disable */
import {ObaAisConsent} from "./oba-ais-consent";

export interface CustomPageImplObaAisConsent {
  content?: Array<ObaAisConsent>;
  firstPage?: boolean;
  lastPage?: boolean;
  nextPage?: boolean;
  number?: number;
  numberOfElements?: number;
  previousPage?: boolean;
  size?: number;
  totalElements?: number;
  totalPages?: number;
}
