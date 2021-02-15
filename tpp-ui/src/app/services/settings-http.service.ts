import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

import { coerceBooleanProperty } from '../commons/utils/utils';
import { Settings } from '../models/settings.model';
import { SettingsService } from './settings.service';

@Injectable({ providedIn: 'root' })
export class SettingsHttpService {
  constructor(
    private http: HttpClient,
    private settingsService: SettingsService
  ) {}

  initializeApp(): Promise<any> {
    return new Promise<void>((resolve) => {
      this.http
        .get('assets/settings.json')
        .toPromise()
        .then((response: Settings) => {
          this.settingsService.settings = response;
          this.settingsService.settings.certGenEnabled = coerceBooleanProperty(
            response.certGenEnabled
          );
          this.settingsService.settings.tppBackendBasePath =
            response.tppBackendBasePath;
          resolve();
        });
    });
  }
}
