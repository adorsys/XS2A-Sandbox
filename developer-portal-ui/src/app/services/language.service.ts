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
