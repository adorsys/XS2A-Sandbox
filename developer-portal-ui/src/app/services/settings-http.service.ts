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
