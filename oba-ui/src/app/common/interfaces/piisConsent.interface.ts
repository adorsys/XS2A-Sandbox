export interface IPsuData {
  additionalPsuIdData: {
    psuAccept: string;
    psuAcceptCharset: string;
    psuAcceptEncoding: string;
    psuAcceptLanguage: string;
    psuDeviceId: string;
    psuGeoLocation: string;
    psuHttpMethod: string;
    psuIpPort: string;
    psuUserAgent: string;
  };
  psuCorporateId: string;
  psuCorporateIdType: string;
  psuId: string;
  psuIdType: string;
  psuIpAddress: string;
}

export interface ICmsPiisConsentAccount {
  aspspAccountId: string;
  bban: number;
  cashAccountType: string;
  currency: string;
  iban: string;
  maskedPan: string;
  msisdn: string;
  other: string;
  pan: string;
  resourceId: string;
}

export interface IPiisConsentContent {
  cmsPiisConsent: {
    account: ICmsPiisConsentAccount;
    cardExpiryDate: string;
    cardInformation: string;
    cardNumber: string;
    consentStatus: string; // status should be an ENUM (for instance RECEIVE)
    creationTimestamp: string;
    expireDate: string;
    id: string;
    instanceId: string;
    lastActionDate: string;
    psuData: IPsuData;
    recurringIndicator: true;
    registrationInformation: string;
    requestDateTime: string;
    statusChangeTimestamp: string;
    tppAuthorisationNumber: string;
  };
  encryptedConsent: string;
}

export interface IPiisConsent {
  content: IPiisConsentContent[];
  firstPage: true;
  lastPage: true;
  nextPage: true;
  number: 0;
  numberOfElements: 0;
  previousPage: true;
  size: 0;
  totalElements: 0;
  totalPages: 0;
}
