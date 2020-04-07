import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {TrackingIdService} from "./tracking-id.service";
import {TrackingId} from "../models/tarckingId.model";
import {CustomizeService} from "./customize.service";
import {Theme} from "../models/theme.model";
import {forkJoin, Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class SettingsHttpService {
  private defaultContentFolderPath = '../assets/content';
  private themeJsonName = '/UITheme.json';
  private customContentFolderPath = '../assets/custom-content';
  private trackingJsonPath = 'assets/content/trackingId.json';

  constructor(private http: HttpClient,
              private trackingIdService: TrackingIdService,
              private customizeService: CustomizeService) {
  }

  initializeApp(): Promise<any> {

    return new Promise((resolve) => {
        this.getThemeUrl().then(
          url => {
            forkJoin([this.http.get(this.trackingJsonPath), this.http.get(url)])
              .toPromise()
              .then(results => {
                this.trackingIdService.trackingId = <TrackingId>results[0];
                this.customizeService.theme = <Theme>results[1];
                localStorage.setItem('currentLanguageFolder', this.customizeService.currentLanguageFolder + '/');
                resolve(true);
              });
          }
        );
      }
    );
  }

  private getThemeUrl(): Promise<string> {
    return this.http.get(this.customContentFolderPath + this.themeJsonName)
      .toPromise()
      .then(
        (theme) => {
          if (this.isThemeValid(theme)) {
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

  private isThemeValid(theme) {
    try {
      JSON.parse(JSON.stringify(theme));
      const errors = CustomizeService.validateTheme(theme);

      if (!errors.length) {
        return true;
      }
    } catch (e) {
      console.log(e);
    }

    return false;
  }

}
