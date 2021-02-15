import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { Settings } from '../models/settings.model';
import { SettingsService } from './settings.service';

@Injectable({providedIn: 'root'})
export class SettingsHttpService {

  constructor(private http: HttpClient, private settingsService: SettingsService) {
  }

  initializeApp(): Promise<any> {

    return new Promise<void>(
      (resolve) => {
        this.http.get('assets/settings.json')
          .toPromise()
          .then(response => {
              this.settingsService.settings = response as Settings;
              resolve();
            }
          )
      }
    );
  }
}
