export interface EnvLink {
  servicesAvailable: ServicesAvailable;
}

export interface ServicesAvailable {
  ledgers: Ledgers;
  XS2AInterfaceSwagger: XS2AInterfaceSwagger;
  developerPortal: DeveloperPortal;
  consentManagementSystem: ConsentManagementSystem;
  ASPSPProfileSwagger: ASPSPProfileSwagger;
  TPPUserInterface: TPPUserInterface;
  onlineBankingUI: OnlineBankingUI;
  onlineBankingBackend: OnlineBankingBackend;
  certificateGenerator: CertificateGenerator;
  footerLogo: FooterLogo;
  gettingStarted: GettingStarted;
  testCase: TestCase;
  faq: Faq;
  contacts: Contacts;
}

export interface Ledgers {
  environmentLink: string;
}

export interface XS2AInterfaceSwagger {
  environmentLink: string;
}

export interface DeveloperPortal {
  environmentLink: string;
}

export interface ConsentManagementSystem {
  environmentLink: string;
}

export interface ASPSPProfileSwagger {
  environmentLink: string;
}

export interface TPPUserInterface {
  environmentLink: string;
}

export interface OnlineBankingUI {
  environmentLink: string;
}

export interface OnlineBankingBackend {
  environmentLink: string;
}

export interface CertificateGenerator {
  environmentLink: string;
}
export interface FooterLogo {
  environmentLink: string;
}
export interface GettingStarted {
  environmentLink: string;
}
export interface TestCase {
  environmentLink: string;
}
export interface Faq {
  environmentLink: string;
}
export interface Contacts {
  environmentLink: string;
}
