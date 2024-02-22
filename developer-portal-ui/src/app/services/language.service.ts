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
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { TranslateService } from '@ngx-translate/core';
import { LocalStorageService } from './local-storage.service';
import { map } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private languageValue = 'en';
  private language = new BehaviorSubject<string>(this.languageValue);

  constructor(private translateService: TranslateService) {}

  setLanguage(language: string) {
    this.translateService.use(language);
    this.languageValue = language;
    LocalStorageService.set('userLanguage', this.languageValue);
    this.language.next(language);

    const ol = document.getElementById('contentTable');
    if (ol) {
      ol.innerHTML = '';
    }
  }

  initializeTranslation() {
    if (LocalStorageService.get('userLanguage')) {
      this.translateService.setDefaultLang(LocalStorageService.get('userLanguage'));
      this.translateService.use(LocalStorageService.get('userLanguage'));
    } else {
      this.translateService.setDefaultLang(this.languageValue);
      this.translateService.use(this.languageValue);
    }
  }

  getLang() {
    if (LocalStorageService.get('userLanguage')) {
      return LocalStorageService.get('userLanguage');
    } else {
      return this.languageValue;
    }
  }

  get currentLanguage(): Observable<string> {
    return this.language.asObservable().pipe(map(() => this.getLang()));
  }
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, localStorage.getItem('currentLanguageFolder'), '.json');
}
