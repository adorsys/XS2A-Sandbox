import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import cssVars from 'css-vars-ponyfill';

@Injectable({
  providedIn: 'root',
})
export class CustomizeService {
  private NEW_THEME_WAS_SET = false;
  private STATUS_WAS_CHANGED = false;
  private IS_CUSTOM = false;
  private DEFAULT_THEME: Theme = {
    globalSettings: {
      logo: 'Logo_XS2ASandbox.png',
      facebook: 'https://www.facebook.com/adorsysGmbH/',
      linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
    },
    contactInfo: {
      img: 'Rene.png',
      name: 'René Pongratz',
      position: 'Software Architect & Expert for API Management',
      email: 'psd2@adorsys.de',
    },
    officesInfo: [
      {
        city: 'Nürnberg',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Fürther Str. 246a, Gebäude 32 im 4.OG',
        addressSecondLine: '90429 Nürnberg',
        phone: '+49(0)911 360698-0',
        email: 'psd2@adorsys.de',
      },
      {
        city: 'Frankfurt',
        company: 'adorsys GmbH & Co. KG',
        addressFirstLine: 'Frankfurter Straße 63 - 69',
        addressSecondLine: '65760 Eschborn',
        email: 'frankfurt@adorsys.de',
        facebook: 'https://www.facebook.com/adorsysGmbH/',
        linkedIn: 'https://www.linkedin.com/company/adorsys-gmbh-&-co-kg/',
      },
    ],
    supportedLanguages: ['en', 'de', 'es', 'ua'],
    supportedApproaches: ['redirect', 'embedded'],
    currency: 'EUR',
  };
  private USER_THEME: Theme = {
    globalSettings: {
      logo: '',
    },
    contactInfo: {
      img: '',
      name: '',
      position: '',
    },
    officesInfo: [
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
      {
        city: '',
        company: '',
        addressFirstLine: '',
        addressSecondLine: '',
      },
    ],
    supportedLanguages: [],
    supportedApproaches: [],
    currency: '',
  };

  private defaultThemeUrl = 'assets/UI/defaultTheme.json';
  private customThemeUrl = '../assets/UI/custom/UITheme.json';

  constructor(private http: HttpClient) {
    this.updateCSS();
  }

  public getCurrency() {
    return this.http
      .get(this.customThemeUrl)
      .toPromise()
      .then(data => {
        let theme = data;
        this.IS_CUSTOM = true;
        try {
          JSON.parse(JSON.stringify(theme));
        } catch (e) {
          theme = this.getDefaultTheme();
          this.IS_CUSTOM = false;
        }
        return theme['currency'];
      })
      .catch(e => {
        this.IS_CUSTOM = false;
        return this.getDefaultTheme().then(data => data.currency);
      });
  }

  public getJSON(): Promise<Theme> {
    return this.http
      .get(this.customThemeUrl)
      .toPromise()
      .then(data => {
        let theme = data;
        this.IS_CUSTOM = true;
        try {
          JSON.parse(JSON.stringify(theme));
          const errors = this.validateTheme(theme);
          if (errors.length) {
            console.log(errors);
            theme = this.getDefaultTheme();
            this.IS_CUSTOM = false;
          }
        } catch (e) {
          console.log(e);
          theme = this.getDefaultTheme();
          this.IS_CUSTOM = false;
        }
        return this.normalizeLanguages(theme as Theme);
      })
      .catch(e => {
        console.log(e);
        this.IS_CUSTOM = false;
        return this.getDefaultTheme().then(data =>
          this.normalizeLanguages(data as Theme)
        );
      });
  }

  private async normalizeLanguages(theme): Promise<Theme> {
    let correctLanguages = [];

    if (theme.supportedLanguages) {
      for (const lang of theme.supportedLanguages) {
        const correctLang = await this.getCorrectLanguage(lang);
        if (correctLang.length > 0) {
          correctLanguages.push(correctLang);
        }
      }

      if (correctLanguages.length == 0) {
        correctLanguages = ['en'];
      }

      theme.supportedLanguages = correctLanguages;
    }

    return theme;
  }

  private getCorrectLanguage(lang: string) {
    return this.http
      .get('../assets/i18n/' + lang + '.json')
      .toPromise()
      .then(data => lang)
      .catch(e => '');
  }

  isCustom() {
    return this.IS_CUSTOM;
  }

  getTheme(type?: string) {
    if (type) {
      return this.DEFAULT_THEME;
    } else {
      return this.USER_THEME;
    }
  }

  getDefaultTheme(): Promise<Theme> {
    return this.http
      .get(this.defaultThemeUrl)
      .toPromise()
      .then(data => {
        return data as Theme;
      });
  }

  setUserTheme(theme: Theme) {
    this.USER_THEME = theme;
    this.updateCSS(this.USER_THEME.globalSettings.cssVariables);
    if (this.isCustom) {
      this.removeExternalLinkElements();
    }
    if (this.USER_THEME.globalSettings.favicon) {
      this.setFavicon(
        this.USER_THEME.globalSettings.favicon.type,
        this.USER_THEME.globalSettings.favicon.href
      );
    }
    this.NEW_THEME_WAS_SET = true;
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
    this.IS_CUSTOM = true;
  }

  getLogo() {
    if (this.NEW_THEME_WAS_SET) {
      return this.USER_THEME.globalSettings.logo;
    } else {
      return this.DEFAULT_THEME.globalSettings.logo;
    }
  }

  validateTheme(theme): string[] {
    const general = ['globalSettings', 'contactInfo', 'officesInfo'];
    const additional = [
      ['logo'],
      ['img', 'name', 'position'],
      ['city', 'company', 'addressFirstLine', 'addressSecondLine'],
      [],
      [],
    ];
    const errors: string[] = [];

    for (let i = 0; i < general.length; i++) {
      if (!theme.hasOwnProperty(general[i])) {
        errors.push(`Missing field ${general[i]}!`);
      } else if (i < 2) {
        for (const property of additional[i]) {
          if (!theme[general[i]].hasOwnProperty(property)) {
            errors.push(`Field ${general[i]} missing property ${property}!`);
          }
        }
      } else {
        for (const office of theme.officesInfo) {
          for (const property of additional[i]) {
            if (!office.hasOwnProperty(property)) {
              errors.push(`Field ${general[i]} missing property ${property}!`);
            }
          }
        }
      }
    }

    return errors;
  }

  private removeExternalLinkElements(): void {
    const linkElements = document.querySelectorAll('link[rel ~= "icon" i]');
    for (const linkElement of Array.from(linkElements)) {
      linkElement.parentNode.removeChild(linkElement);
    }
  }

  private addFavicon(type: string, href: string): void {
    const linkElement = document.createElement('link');
    linkElement.setAttribute('id', 'customize-service-injected-node');
    linkElement.setAttribute('rel', 'icon');
    linkElement.setAttribute('type', type);
    linkElement.setAttribute('href', href);
    document.head.appendChild(linkElement);
  }

  private removeFavicon(): void {
    const linkElement = document.head.querySelector(
      '#customize-service-injected-node'
    );
    if (linkElement) {
      document.head.removeChild(linkElement);
    }
  }

  private setFavicon(type: string, href: string): void {
    this.removeFavicon();
    this.addFavicon(type, href);
  }

  private updateCSS(variables: CSSVariables = {}) {
    // Use css-vars-ponyfill to polyfill css-variables for legacy browser
    cssVars({
      include: 'style',
      onlyLegacy: true,
      watch: true,
      variables,
      onComplete(cssText, styleNode, cssVariables) {
        console.log(cssText, styleNode, cssVariables);
      },
    });
    // If you decide to drop ie11, edge < 14 support in future, use this as implementation to set variables
    // Object.keys(variables).forEach(variableName => {
    //   document.documentElement.style.setProperty('--' + variableName, variables[variableName]);
    // });
  }
}

export interface Theme {
  globalSettings: GlobalSettings;
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];
  supportedLanguages: string[];
  supportedApproaches: string[];
  currency: string;
}

export interface GlobalSettings {
  logo: string;
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
