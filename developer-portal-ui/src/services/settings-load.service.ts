import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { SettingsService } from './settings.service';
import { EnvLink } from '../models/envLinks.model';

@Injectable({
  providedIn: 'root',
})
export class SettingsLoadService {
  constructor(
    private http: HttpClient,
    private settingsService: SettingsService
  ) {}

  initializeApp(): Promise<any> {
    return new Promise(resolve => {
      this.http
        .get('assets/UI/envLinks.json')
        .toPromise()
        .then(settings => {
          if (settings instanceof Array) {
            for (let i = 0; i < settings.length; i++) {
              if (!(settings[i] instanceof EnvLink)) {
                this.settingsService.fallbackToDefault();
                break;
              }
            }
            this.settingsService.settings.envLinks = settings as EnvLink[];
          } else {
            this.settingsService.fallbackToDefault();
          }

          resolve();
        });
    });
  }
}
