import {Injectable} from '@angular/core';
import {BehaviorSubject} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {TranslateHttpLoader} from "@ngx-translate/http-loader";
import {TranslateService} from "@ngx-translate/core";

@Injectable({
  providedIn: 'root',
})
export class LanguageService {
  private languageValue = 'en';
  private language = new BehaviorSubject<string>(this.languageValue);
  currentLanguage = this.language.asObservable();

  constructor(private translateService: TranslateService) {
  }

  setLanguage(language: string) {
    this.language.next(language);
    this.translateService.use(language);
    this.languageValue = language;

    const ol = document.getElementById('contentTable');
    if (ol) {
      ol.innerHTML = '';
    }
  }

  initializeTranslation() {
    this.translateService.setDefaultLang(this.languageValue);
    this.translateService.use(this.languageValue);
  }

  getLang() {
    return this.languageValue;
  }
}

export function HttpLoaderFactory(http: HttpClient) {
  return new TranslateHttpLoader(http, localStorage.getItem('currentLanguageFolder'), '.json');
}
