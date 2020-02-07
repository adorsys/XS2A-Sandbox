export interface PaymentTypesMatrix {
  payments: Array<string>;
  'periodic-payments': Array<string>;
  'bulk-payments': Array<string>;
}

export enum PaymentType {
  single = 'payments',
  periodic = 'periodic-payments',
  bulk = 'bulk-payments',
}
