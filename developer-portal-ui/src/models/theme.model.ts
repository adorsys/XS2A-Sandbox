
export interface Theme {
  globalSettings: GlobalSettings;
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];
  supportedLanguages: string[];
  supportedApproaches: string[];
  currency: string;
  tppSettings: TppSettings;
}

export interface GlobalSettings {
  logo: string;
  footerLogo: string;
  favicon?: Favicon;
  facebook?: string;
  linkedIn?: string;
  cssVariables?: CSSVariables;
}

export interface Favicon {
  type: string;
  href: string;
}

export interface CSSVariables {
  [key: string]: string;

  colorPrimary?: string;
  colorSecondary?: string;
  fontFamily?: string;
  bodyBG?: string;
  bodyFontColor?: string;
  headerBG?: string;
  headerFontColor?: string;
  mainBG?: string;
  footerBG?: string;
  footerFontColor?: string;
  anchorFontColor?: string;
  anchorFontColorHover?: string;
  heroBG?: string;
  stepBG?: string;
  contactsCardBG?: string;
  testCasesLeftSectionBG?: string;
  testCasesRightSectionBG?: string;
  buttonBG?: string;
  buttonTextColor?: string;
  buttonHovered?: string;
  buttonClicked?: string;
  h1FontSize?: string,
  h1FontWeight?: string,
  h2FontSize?: string,
  h2FontWeight?: string,
  h3FontSize?: string,
  h3FontWeight?: string
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
  facebook?: string;
  linkedIn?: string;
}

export interface TppSettings {
  tppDefaultNokRedirectUrl: string;
  tppDefaultRedirectUrl: string;
}
