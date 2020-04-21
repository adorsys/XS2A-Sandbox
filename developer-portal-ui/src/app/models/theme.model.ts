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
  supportedApproaches?: string[];
  examplesCurrency?: string;
  tppSettings?: TppSettings;
}

export interface HomePageSettings {
  contactInfo?: ContactInfo;
  showProductHistory?: boolean;
  showSlider?: boolean;
}
