/*
 * Copyright 2018-2022 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

export interface EnvLink {
  servicesAvailable: ServicesAvailable;
}

export interface ServicesAvailable {
  ledgers: Ledgers;
  XS2AInterfaceSwagger: XS2AInterfaceSwagger;
  XS2AInterfaceSwagger2: XS2AInterfaceSwagger2;
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

export interface XS2AInterfaceSwagger2 {
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
