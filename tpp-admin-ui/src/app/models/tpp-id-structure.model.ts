export interface TppIdStructure {
  length: number;
  type: TppIdType;
}

export enum TppIdType {
  n = 'digits (numeric characters 0 to 9 only)',
  a = 'upper case letters (alphabetic characters A-Z only)',
  c = 'upper and lower case alphanumeric characters (A-Z, a-z and 0-9)',
}

export enum TppIdPatterns {
  n = '^[0-9]*$',
  a = '^[A-Z]*$',
  c = '^[A-Za-z0-9]*$',
}
