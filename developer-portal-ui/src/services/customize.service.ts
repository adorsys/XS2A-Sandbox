import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class CustomizeService {
  private NEW_THEME_WAS_SET = false;
  private STATUS_WAS_CHANGED = false;
  private DEFAULT_THEME: Theme = {
    globalSettings: {
      logo: '../assets/UI/Logo_XS2ASandbox.png',
      fontFamily: 'Arial, sans-serif',
      headerBG: '#ffffff',
      headerFontColor: '#000000',
      footerBG: '#054f72',
      footerFontColor: '#ffffff',
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
  };
  private USER_THEME: Theme = {
    globalSettings: {
      logo: '',
      fontFamily: '',
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
  };

  constructor(private http: HttpClient) {}

  public getJSON(): Promise<Theme> {
    return this.http
      .get('../assets/UI/UITheme.json')
      .toPromise()
      .then(data => {
        let theme = data;
        try {
          JSON.parse(JSON.stringify(theme));
          const errors = this.validateTheme(theme);
          if (errors.length) {
            console.log(errors);
            theme = this.getDefaultTheme();
          }
        } catch (e) {
          console.log(e);
          theme = this.getDefaultTheme();
        }
        return theme as Theme;
      })
      .catch(e => {
        console.log(e);
        return this.getDefaultTheme();
      });
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
      .get('../assets/UI/defaultTheme.json')
      .toPromise()
      .then(data => {
        return data as Theme;
      });
  }

  setDefaultTheme() {
    this.NEW_THEME_WAS_SET = false;
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
    this.changeFontFamily();
    this.USER_THEME = {
      globalSettings: {
        logo: '',
        fontFamily: '',
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
    };
  }

  setUserTheme(theme: Theme) {
    this.USER_THEME = theme;
    this.changeFontFamily(this.USER_THEME.globalSettings.fontFamily);
    this.NEW_THEME_WAS_SET = true;
    this.STATUS_WAS_CHANGED = !this.STATUS_WAS_CHANGED;
  }

  getLogo() {
    if (this.NEW_THEME_WAS_SET) {
      return this.USER_THEME.globalSettings.logo;
    } else {
      return this.DEFAULT_THEME.globalSettings.logo;
    }
  }

  changeFontFamily(value?: string) {
    if (value) {
      this.USER_THEME.globalSettings.fontFamily = value;
      const body = document.getElementsByTagName('body');
      body[0].setAttribute(
        'style',
        `font-family: ${this.USER_THEME.globalSettings.fontFamily}`
      );
    } else {
      const body = document.getElementsByTagName('body');
      body[0].setAttribute(
        'style',
        `font-family: ${this.DEFAULT_THEME.globalSettings.fontFamily}`
      );
    }
  }

  validateTheme(theme): string[] {
    const general = ['globalSettings', 'contactInfo', 'officesInfo'];
    const additional = [
      ['logo'],
      ['img', 'name', 'position'],
      ['city', 'company', 'addressFirstLine', 'addressSecondLine'],
    ];
    const errors: string[] = [];

    for (let i = 0; i < general.length; i++) {
      if (!theme.hasOwnProperty(general[i])) {
        errors.push(`Missing field ${general[i]}!`);
      } else if (i !== 2) {
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
}

export interface Theme {
  globalSettings: GlobalSettings;
  contactInfo: ContactInfo;
  officesInfo: OfficeInfo[];
}

export interface GlobalSettings {
  logo: string;
  fontFamily?: string;
  headerBG?: string;
  headerFontColor?: string;
  footerBG?: string;
  footerFontColor?: string;
  facebook?: string;
  linkedIn?: string;
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
