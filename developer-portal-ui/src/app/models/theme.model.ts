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

export interface Theme {
  globalSettings: GlobalSettings;
  pagesSettings: PagesSettings;
}

export interface GlobalSettings {
  supportedLanguagesDictionary: object;
  favicon?: string;
  socialMedia?: object;
  cssVariables?: CSSVariables;
  googleAnalyticsTrackingId?: string;
}

export interface PagesSettings {
  playWithDataSettings?: PlayWithDataSettings;
  footerSettings: NavigationSettings;
  navigationBarSettings: NavigationSettings;
  contactPageSettings?: ContactPageSettings;
  homePageSettings?: HomePageSettings;
}

export interface CSSVariables {
  [key: string]: string;
}

export interface ContactInfo {
  img: string;
  name: string;
  position: string;
  email?: string;
  phone?: string;
}

export interface OfficeInfo {
  city: string;
  company: string;
  addressFirstLine: string;
  addressSecondLine: string;
  phone?: string;
  email?: string;
}

export interface TppSettings {
  tppDefaultNokRedirectUrl?: string;
  tppDefaultRedirectUrl?: string;
}

export interface NavigationSettings {
  allowedNavigationSize: number;
  logo: string;
  logoLink?: string;
}

class ContactPageSettings {
  contactInfo?: ContactInfo;
  officesInfo?: OfficeInfo[];
}

class PlayWithDataSettings {
  fundConfirmationSupported?: boolean;
  supportedApproaches?: string[];
  examplesCurrency?: string;
  tppSettings?: TppSettings;
}

export interface HomePageSettings {
  contactInfo?: ContactInfo;
  showProductHistory?: boolean;
  showSlider?: boolean;
}
