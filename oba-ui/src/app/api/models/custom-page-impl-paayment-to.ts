/* tslint:disable */
import {PaymentTO} from "./payment-to";

export interface CustomPageImplPaymentTO {
  content?: Array<PaymentTO>;
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
