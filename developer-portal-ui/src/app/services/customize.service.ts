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

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import cssVars from 'css-vars-ponyfill';
import { CSSVariables, Theme } from '../models/theme.model';
import { of } from 'rxjs';
import { SUPPORTED_SOCIAL_MEDIA } from '../components/common/constant/constants';

@Injectable({
  providedIn: 'root',
})
export class CustomizeService {
  private _defaultContentFolderPath = '../assets/content';
  private _customContentFolderPath = '../assets/custom-content';
  private _custom = false;
  private languagesFolder = '/i18n';

  currentTheme;

  constructor(private http: HttpClient) {}

  private static addFavicon(type: string, href: string) {
    const linkElement = document.createElement('link');
    linkElement.setAttribute('id', 'customize-service-injected-node');
    linkElement.setAttribute('rel', 'icon');
    linkElement.setAttribute('type', type);
    linkElement.setAttribute('href', href);
    document.head.appendChild(linkElement);
  }

  private static removeFavicon() {
    const linkElement = document.head.querySelector('#customize-service-injected-node');
    if (linkElement) {
      document.head.removeChild(linkElement);
    }
  }

  private static setFavicon(type: string, href: string): void {
    CustomizeService.removeFavicon();
    CustomizeService.addFavicon(type, href);
  }

  private static removeExternalLinkElements() {
    const linkElements = document.querySelectorAll('link[rel ~= "icon"]');
    for (const linkElement of Array.from(linkElements)) {
      linkElement.parentNode.removeChild(linkElement);
    }
  }

  set theme(theme: Theme) {
    if (theme) {
      this.currentTheme = of(this.setUpRoutes(theme));
    } else {
      this.currentTheme = of({});
    }
  }

  get custom(): boolean {
    return this._custom;
  }

  get currentLanguageFolder(): string {
    return (this.custom ? this._customContentFolderPath : this._defaultContentFolderPath) + this.languagesFolder;
  }

  get defaultContentFolder(): string {
    return this._defaultContentFolderPath;
  }

  get customContentFolder(): string {
    return this._customContentFolderPath;
  }

  getIconClassForSocialMedia(social: any): string {
    return `social-media-icon fab ${SUPPORTED_SOCIAL_MEDIA[social]}`;
  }

  setCustom() {
    this._custom = true;
  }

  setStyling(theme: Theme) {
    if (theme.globalSettings.cssVariables) {
      this.updateCSS(theme.globalSettings.cssVariables);
    }

    if (theme.globalSettings.favicon) {
      CustomizeService.removeExternalLinkElements();
      CustomizeService.setFavicon('image/x-icon', theme.globalSettings.favicon);
    }
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

  private setUpRoutes(theme: Theme) {
    const folder = this._custom ? this._customContentFolderPath : this._defaultContentFolderPath;

    if (theme.globalSettings && theme.globalSettings.favicon) {
      theme.globalSettings.favicon = `${folder}/${theme.globalSettings.favicon}`;
    }

    const pagesSettings = theme.pagesSettings;
    if (pagesSettings) {
      if (pagesSettings.navigationBarSettings && pagesSettings.navigationBarSettings.logo) {
        pagesSettings.navigationBarSettings.logo = `${folder}/${pagesSettings.navigationBarSettings.logo}`;
      }

      if (pagesSettings.footerSettings && pagesSettings.footerSettings.logo) {
        pagesSettings.footerSettings.logo = `${folder}/${pagesSettings.footerSettings.logo}`;
      }

      if (
        pagesSettings.contactPageSettings &&
        pagesSettings.contactPageSettings.contactInfo &&
        pagesSettings.contactPageSettings.contactInfo.img
      ) {
        pagesSettings.contactPageSettings.contactInfo.img = `${folder}/${pagesSettings.contactPageSettings.contactInfo.img}`;
      }

      if (
        pagesSettings.homePageSettings &&
        pagesSettings.homePageSettings.contactInfo &&
        pagesSettings.contactPageSettings.contactInfo.img
      ) {
        pagesSettings.homePageSettings.contactInfo.img = `${folder}/${pagesSettings.homePageSettings.contactInfo.img}`;
      }
    }

    return theme;
  }

  async normalizeLanguages(theme: Theme) {
    let correctLanguages = {};
    const defaultLanguages = {
      en: `${this._defaultContentFolderPath}${this.languagesFolder}/en/united-kingdom.png`,
      de: `${this._defaultContentFolderPath}${this.languagesFolder}/de/germany.png`,
      ua: `${this._defaultContentFolderPath}${this.languagesFolder}/ua/ukraine.png`,
    };

    if (theme.globalSettings && theme.globalSettings.supportedLanguagesDictionary && this.custom) {
      for (const lang of Object.keys(theme.globalSettings.supportedLanguagesDictionary)) {
        const languageLink = `${this._customContentFolderPath}${this.languagesFolder}/${lang}/${theme.globalSettings.supportedLanguagesDictionary[lang]}`;
        const correctLang = await this.getCorrectLanguage(lang, languageLink);
        if (correctLang.length > 0) {
          correctLanguages[lang] = languageLink;
        }
      }

      if (Object.keys(correctLanguages).length === 0) {
        correctLanguages = defaultLanguages;
      }

      theme.globalSettings.supportedLanguagesDictionary = correctLanguages;
    } else {
      theme.globalSettings = {
        supportedLanguagesDictionary: defaultLanguages,
      };
    }

    return theme;
  }

  private getCorrectLanguage(lang: string, languageLink: string) {
    return this.http
      .get(languageLink, { responseType: 'blob' })
      .toPromise()
      .then(() => lang)
      .catch(() => {
        console.log(`Could not parse image for language ${lang}, language is excluded from list.`);
        return '';
      });
  }
}
