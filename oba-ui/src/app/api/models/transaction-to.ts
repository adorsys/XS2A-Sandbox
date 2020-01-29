/* tslint:disable */
import { AmountTO } from './amount-to';
import { AccountReferenceTO } from './account-reference-to';
import { ExchangeRateTO } from './exchange-rate-to';
import { RemittanceInformationStructuredTO } from './remittance-information-structured-to';
export interface TransactionTO {
  entryReference?: string;
  amount?: AmountTO;
  bookingDate?: string;
  checkId?: string;
  creditorAccount?: AccountReferenceTO;
  creditorId?: string;
  creditorName?: string;
  debtorAccount?: AccountReferenceTO;
  debtorName?: string;
  endToEndId?: string;
  bankTransactionCode?: string;
  exchangeRate?: Array<ExchangeRateTO>;
  mandateId?: string;
  proprietaryBankTransactionCode?: string;
  purposeCode?: string;
  remittanceInformationStructured?: RemittanceInformationStructuredTO;
  remittanceInformationUnstructured?: string;
  transactionId?: string;
  ultimateCreditor?: string;
  ultimateDebtor?: string;
  valueDate?: string;
}
