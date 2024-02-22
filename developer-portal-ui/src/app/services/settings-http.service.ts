/*
 * Copyright 2018-2024 adorsys GmbH & Co KG
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
 * contact us at sales@adorsys.com.
 */

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CustomizeService } from './customize.service';
import { Theme } from '../models/theme.model';

@Injectable({
  providedIn: 'root',
})
export class SettingsHttpService {
  private defaultContentFolderPath = '../assets/content';
  private themeJsonName = '/UITheme.json';
  private customContentFolderPath = '../assets/custom-content';

  constructor(private http: HttpClient, private customizeService: CustomizeService) {}

  private static isThemeValid(theme) {
    try {
      JSON.parse(JSON.stringify(theme));
      return true;
    } catch (e) {
      console.log(e);
    }

    return false;
  }

  initializeApp(): Promise<any> {
    return new Promise((resolve) => {
      this.getThemeUrl().then((url) => {
        this.http
          .get(url)
          .toPromise()
          .then((data: Theme) => {
            this.customizeService.theme = data;
            localStorage.setItem('currentLanguageFolder', this.customizeService.currentLanguageFolder + '/');
            resolve(true);
          });
      });
    });
  }

  private getThemeUrl(): Promise<string> {
    return this.http
      .get(this.customContentFolderPath + this.themeJsonName)
      .toPromise()
      .then(
        (theme) => {
          if (SettingsHttpService.isThemeValid(theme)) {
            this.customizeService.setCustom();
            return this.customContentFolderPath + this.themeJsonName;
          }

          return this.defaultContentFolderPath + this.themeJsonName;
        },
        () => {
          return this.defaultContentFolderPath + this.themeJsonName;
        }
      );
  }
}
