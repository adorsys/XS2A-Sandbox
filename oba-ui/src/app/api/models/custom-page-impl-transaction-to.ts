/* tslint:disable */
import { TransactionTO } from './transaction-to';
export interface CustomPageImplTransactionTO {
  content?: Array<TransactionTO>;
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
